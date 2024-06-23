package hyuuny.fooddelivery.application.order

import AdminOrderSearchCondition
import ApiOrderSearchCondition
import CreateOrderCommand
import CreateOrderItemCommand
import CreateOrderItemOptionCommand
import CreateOrderRequest
import UpdateOrderStatusCommand
import generateOrderNumber
import generatePaymentId
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.log.Log
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.domain.order.OrderItem
import hyuuny.fooddelivery.domain.order.OrderItemOption
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.order.OrderItemOptionRepository
import hyuuny.fooddelivery.infrastructure.order.OrderItemRepository
import hyuuny.fooddelivery.infrastructure.order.OrderRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class OrderUseCase(
    private val repository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderItemOptionRepository: OrderItemOptionRepository,
    private val orderCartValidator: OrderCartValidator,
) {

    companion object : Log

    suspend fun getOrderByAdminCondition(
        searchCondition: AdminOrderSearchCondition,
        pageable: Pageable
    ): PageImpl<Order> {
        val page = repository.findAllOrders(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun getOrdersByApiCondition(searchCondition: ApiOrderSearchCondition, pageable: Pageable): PageImpl<Order> {
        val page = repository.findAllOrders(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    @Transactional
    suspend fun createOrder(
        cartId: Long,
        request: CreateOrderRequest,
        getUser: suspend () -> User,
        getMenus: suspend () -> List<Menu>,
        getOptions: suspend () -> List<Option>,
    ): Order {
        orderCartValidator.validate(cartId, request)

        val now = LocalDateTime.now()
        val user = getUser()
        val order = Order.handle(
            CreateOrderCommand(
                orderNumber = generateOrderNumber(now),
                userId = user.id!!,
                storeId = request.storeId,
                categoryId = request.categoryId,
                paymentId = generatePaymentId(),
                paymentMethod = request.paymentMethod,
                status = OrderStatus.CREATED,
                deliveryType = request.deliveryType,
                zipCode = request.zipCode,
                address = request.address,
                detailAddress = request.detailAddress,
                phoneNumber = request.phoneNumber,
                messageToRider = request.messageToRider,
                messageToStore = request.messageToStore,
                totalPrice = request.totalPrice,
                deliveryFee = request.deliveryFee,
                createdAt = now,
                updatedAt = now,
            )
        )
        val savedOrder = repository.insert(order)

        val menuMap = getMenus().associateBy { it.id }
        val optionMap = getOptions().associateBy { it.id }
        request.orderItems.map { item ->
            val menu = menuMap[item.menuId] ?: throw IllegalStateException("${item.menuId}번 메뉴를 찾을 수 없습니다.")
            val orderItem = OrderItem.handle(
                CreateOrderItemCommand(
                    orderId = savedOrder.id!!,
                    menuId = menu.id!!,
                    menuName = menu.name,
                    menuPrice = menu.price,
                    quantity = item.quantity,
                    createdAt = now,
                )
            )
            val savedOrderItem = orderItemRepository.insert(orderItem)

            val orderItemOptions = item.optionIds.map {
                val option = optionMap[it] ?: throw IllegalStateException("${it}번 옵션을 찾을 수 없습니다.")
                OrderItemOption.handle(
                    CreateOrderItemOptionCommand(
                        orderItemId = savedOrderItem.id!!,
                        optionId = option.id!!,
                        optionName = option.name,
                        optionPrice = option.price,
                        createdAt = now,
                    )
                )
            }
            orderItemOptionRepository.insertAll(orderItemOptions)
        }

        log.info("Create Order Number: ${order.orderNumber}")
        return savedOrder
    }

    suspend fun getOrder(id: Long, getUser: suspend () -> User): Order {
        val user = getUser()
        return findOrderByIdAndUserIdOrThrow(id, user.id!!)
    }

    suspend fun getOrder(id: Long): Order = findOrderByIdOrThrow(id)

    @Transactional
    suspend fun cancelOrder(id: Long, getUser: suspend () -> User) {
        val user = getUser()
        val order = repository.findByIdAndUserId(id, user.id!!) ?: throw NoSuchElementException("${id}번 주문을 찾을 수 없습니다.")
        if (!order.isCancelable()) throw IllegalStateException("주문 취소가 불가능합니다.")

        val now = LocalDateTime.now()
        order.handle(
            UpdateOrderStatusCommand(
                status = OrderStatus.CANCELLED_BY_USER,
                updatedAt = now,
            )
        )

        log.info("Cancel Order Number: ${order.orderNumber}")
        repository.updateStatus(order)
    }

    @Transactional
    suspend fun refundOrder(id: Long, getUser: suspend () -> User) {
        val user = getUser()
        val order = repository.findByIdAndUserId(id, user.id!!) ?: throw NoSuchElementException("${id}번 주문을 찾을 수 없습니다.")
        if (!order.isRefundable()) throw IllegalStateException("주문 환불이 불가능합니다.")

        val now = LocalDateTime.now()
        order.handle(
            UpdateOrderStatusCommand(
                status = OrderStatus.REFUNDED,
                updatedAt = now,
            )
        )

        log.info("Refund Order Number: ${order.orderNumber}")
        repository.updateStatus(order)
    }

    private suspend fun findOrderByIdAndUserIdOrThrow(id: Long, userId: Long) =
        repository.findByIdAndUserId(id, userId) ?: throw NoSuchElementException("${id}번 주문을 찾을 수 없습니다.")

    private suspend fun findOrderByIdOrThrow(id: Long) =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 주문을 찾을 수 없습니다.")

}
