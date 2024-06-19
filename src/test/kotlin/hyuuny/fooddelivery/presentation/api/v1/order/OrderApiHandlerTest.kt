package hyuuny.fooddelivery.presentation.api.v1.order

import CreateOrderItemRequest
import CreateOrderRequest
import com.ninjasquad.springmockk.MockkBean
import generateOrderNumber
import generatePaymentId
import hyuuny.fooddelivery.application.cart.CartUseCase
import hyuuny.fooddelivery.application.order.OrderItemOptionUseCase
import hyuuny.fooddelivery.application.order.OrderItemUseCase
import hyuuny.fooddelivery.application.order.OrderUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.domain.order.OrderItem
import hyuuny.fooddelivery.domain.order.OrderItemOption
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderResponse
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class OrderApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: OrderUseCase

    @MockkBean
    private lateinit var orderItemUseCase: OrderItemUseCase

    @MockkBean
    private lateinit var orderItemOptionUseCase: OrderItemOptionUseCase

    @MockkBean
    private lateinit var cartUseCase: CartUseCase

    @DisplayName("회원은 주문을 생성할 수 있다.")
    @Test
    fun createOrder() {
        val id = 1L
        val userId = 1L
        val storeId = 7L
        val cartId = 3L

        val request = CreateOrderRequest(
            storeId = storeId,
            paymentMethod = PaymentMethod.NAVER_PAY,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = "리뷰이벤트 참여합니다 !",
            totalPrice = 23000,
            deliveryFee = 0,
            orderItems = listOf(
                CreateOrderItemRequest(menuId = 1L, quantity = 2, optionIds = listOf(1L, 2L)),
                CreateOrderItemRequest(menuId = 2L, quantity = 1, optionIds = listOf(3L)),
            )
        )

        val now = LocalDateTime.now()
        val order = generateOrder(id, userId, request, now)
        val orderItems = generateOrderItems(now)
        val orderItemOptions = generateOrderItemOptions(now)

        coEvery { useCase.createOrder(any(), any(), any(), any(), any()) } returns order
        coEvery { orderItemUseCase.getAllByOrderId(any()) } returns orderItems
        coEvery { orderItemOptionUseCase.getAllByOrderItemIdIn(any()) } returns orderItemOptions
        coEvery { cartUseCase.clearCart(any()) } returns Unit

        val orderItemResponses = orderItems.map { orderItem ->
            val itemOptions = orderItemOptions.filter { it.orderItemId == orderItem.id }
                .map { OrderItemOptionResponse.from(it) }
            OrderItemResponse.from(orderItem, itemOptions)
        }
        val expectedResponse = OrderResponse.from(order, orderItemResponses)

        webTestClient.post().uri("/api/v1/users/$userId/carts/$cartId/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.orderNumber").isEqualTo(expectedResponse.orderNumber)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId)
            .jsonPath("$.paymentId").isEqualTo(expectedResponse.paymentId)
            .jsonPath("$.paymentMethod").isEqualTo(expectedResponse.paymentMethod.name)
            .jsonPath("$.status").isEqualTo(expectedResponse.status.name)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
            .jsonPath("$.zipCode").isEqualTo(expectedResponse.zipCode)
            .jsonPath("$.address").isEqualTo(expectedResponse.address)
            .jsonPath("$.detailAddress").isEqualTo(expectedResponse.detailAddress)
            .jsonPath("$.phoneNumber").isEqualTo(expectedResponse.phoneNumber)
            .jsonPath("$.messageToRider").isEqualTo(expectedResponse.messageToRider!!)
            .jsonPath("$.messageToStore").isEqualTo(expectedResponse.messageToStore!!)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
            .jsonPath("$.orderItems").isArray
            .jsonPath("$.orderItems[0].id").isEqualTo(expectedResponse.orderItems[0].id)
            .jsonPath("$.orderItems[0].menuId").isEqualTo(expectedResponse.orderItems[0].menuId)
            .jsonPath("$.orderItems[0].menuName").isEqualTo(expectedResponse.orderItems[0].menuName)
            .jsonPath("$.orderItems[0].price").isEqualTo(expectedResponse.orderItems[0].price)
            .jsonPath("$.orderItems[0].quantity").isEqualTo(expectedResponse.orderItems[0].quantity)
            .jsonPath("$.orderItems[0].options").isArray
            .jsonPath("$.orderItems[0].options[0].id").isEqualTo(expectedResponse.orderItems[0].options[0].id)
            .jsonPath("$.orderItems[0].options[0].orderItemId")
            .isEqualTo(expectedResponse.orderItems[0].options[0].orderItemId)
            .jsonPath("$.orderItems[0].options[0].optionId")
            .isEqualTo(expectedResponse.orderItems[0].options[0].optionId)
            .jsonPath("$.orderItems[0].options[0].optionName")
            .isEqualTo(expectedResponse.orderItems[0].options[0].optionName)
            .jsonPath("$.orderItems[0].options[0].price").isEqualTo(expectedResponse.orderItems[0].options[0].price)
            .jsonPath("$.orderItems[0].options[1].id").isEqualTo(expectedResponse.orderItems[0].options[1].id)
            .jsonPath("$.orderItems[0].options[1].orderItemId")
            .isEqualTo(expectedResponse.orderItems[0].options[1].orderItemId)
            .jsonPath("$.orderItems[0].options[1].optionId")
            .isEqualTo(expectedResponse.orderItems[0].options[1].optionId)
            .jsonPath("$.orderItems[0].options[1].optionName")
            .isEqualTo(expectedResponse.orderItems[0].options[1].optionName)
            .jsonPath("$.orderItems[0].options[1].price").isEqualTo(expectedResponse.orderItems[0].options[1].price)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
    }

    private fun generateOrder(id: Long, userId: Long, request: CreateOrderRequest, now: LocalDateTime): Order {
        val orderNumber = generateOrderNumber(now)
        val paymentId = generatePaymentId()
        return Order(
            id = id,
            orderNumber = orderNumber,
            userId = userId,
            storeId = request.storeId,
            paymentId = paymentId,
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
    }

    private fun generateOrderItems(now: LocalDateTime): List<OrderItem> = listOf(
        OrderItem(1L, 1L, 1L, "피자", 15000, 1, now),
        OrderItem(2L, 1L, 2L, "불고기 버거", 5000, 1, now),
    )

    private fun generateOrderItemOptions(now: LocalDateTime): List<OrderItemOption> = listOf(
        OrderItemOption(1L, 1L, 1L, "페퍼로니", 1000, now),
        OrderItemOption(2L, 1L, 2L, "제로콜라", 1500, now),
        OrderItemOption(3L, 2L, 3L, "콘 아이스크림", 500, now),
    )

}
