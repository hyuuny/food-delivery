package hyuuny.fooddelivery.presentation.api.v1.order

import ApiOrderSearchCondition
import CreateOrderRequest
import hyuuny.fooddelivery.application.cart.CartUseCase
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.order.OrderUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.convertToLocalDate
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class OrderApiHandler(
    private val useCase: OrderUseCase,
    private val menuUseCase: MenuUseCase,
    private val userUseCase: UserUseCase,
    private val optionUseCase: OptionUseCase,
    private val cartUseCase: CartUseCase,
    private val responseMapper: OrderResponseMapper,
) {

    suspend fun getOrders(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val categoryIds = request.queryParamOrNull("categoryIds")
            ?.let { it.split(",").mapNotNull { it.toLongOrNull() } }
        val deliveryType = request.queryParamOrNull("deliveryType")?.takeIf { it.isNotBlank() }
            ?.let { DeliveryType.valueOf(it.uppercase().trim()) }
        val orderStatus = request.queryParamOrNull("orderStatus")?.takeIf { it.isNotBlank() }
            ?.let { OrderStatus.valueOf(it.uppercase().trim()) }
        val storeName = request.queryParamOrNull("storeName")?.takeIf { it.isNotBlank() }
        val menuName = request.queryParamOrNull("menuName")?.takeIf { it.isNotBlank() }
        val fromDate = request.queryParamOrNull("fromDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val toDate = request.queryParamOrNull("toDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val searchCondition = ApiOrderSearchCondition(
            userId = userId,
            categoryIds = categoryIds,
            deliveryType = deliveryType,
            orderStatus = orderStatus,
            storeName = storeName,
            menuName = menuName,
            fromDate = fromDate,
            toDate = toDate,
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getOrdersByApiCondition(searchCondition, pageRequest)

        val orderResponses = responseMapper.mapToOrderResponses(page.content)
        val response = SimplePage(orderResponses, page)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun createOrder(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val cartId = request.pathVariable("cartId").toLong()
        val body = request.awaitBody<CreateOrderRequest>()

        val order = useCase.createOrder(
            cartId = cartId,
            request = body,
            getUser = { userUseCase.getUser(userId) },
            getMenus = { menuUseCase.getAllByIds(body.orderItems.map { it.menuId }) },
            getOptions = { optionUseCase.getAllByIds(body.orderItems.flatMap { it.optionIds }) },
        )
        cartUseCase.clearCart(cartId)
        val response = responseMapper.mapToOrderResponse(order)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getOrder(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val id = request.pathVariable("id").toLong()

        val order = useCase.getOrder(
            id = id,
            getUser = { userUseCase.getUser(userId) }
        )
        val response = responseMapper.mapToOrderResponse(order)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun cancelOrder(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val id = request.pathVariable("id").toLong()

        useCase.cancelOrder(id) { userUseCase.getUser(userId) }
        return ok().bodyValueAndAwait(mapOf("message" to "${id}번 주문이 정상적으로 취소되었습니다."))
    }

    suspend fun refundOrder(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val id = request.pathVariable("id").toLong()

        useCase.refundOrder(id) { userUseCase.getUser(userId) }
        return ok().bodyValueAndAwait(mapOf("message" to "${id}번 주문이 정상적으로 환불되었습니다."))
    }


}
