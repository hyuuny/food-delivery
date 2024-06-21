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
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.domain.order.OrderItem
import hyuuny.fooddelivery.domain.order.OrderItemOption
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderResponse
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class OrderApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: OrderUseCase

    @MockkBean
    private lateinit var orderItemUseCase: OrderItemUseCase

    @MockkBean
    private lateinit var orderItemOptionUseCase: OrderItemOptionUseCase

    @MockkBean
    private lateinit var cartUseCase: CartUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

    @DisplayName("회원은 주문을 생성할 수 있다.")
    @Test
    fun createOrder() {
        val id = 1L
        val userId = 1L
        val storeId = 7L
        val categoryId = 3L
        val cartId = 3L

        val request = CreateOrderRequest(
            storeId = storeId,
            categoryId = categoryId,
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
            .jsonPath("$.categoryId").isEqualTo(expectedResponse.categoryId)
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

    @DisplayName("회원은 주문 내역을 상세조회 할 수 있다.")
    @Test
    fun getOrder() {
        val id = 32L
        val userId = 1L
        val storeId = 7L
        val categoryId = 3L

        val request = CreateOrderRequest(
            storeId = storeId,
            categoryId = categoryId,
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

        coEvery { useCase.getOrder(any(), any()) } returns order
        coEvery { orderItemUseCase.getAllByOrderId(any()) } returns orderItems
        coEvery { orderItemOptionUseCase.getAllByOrderItemIdIn(any()) } returns orderItemOptions

        val orderItemResponses = orderItems.map { orderItem ->
            val itemOptions = orderItemOptions.filter { it.orderItemId == orderItem.id }
                .map { OrderItemOptionResponse.from(it) }
            OrderItemResponse.from(orderItem, itemOptions)
        }
        val expectedResponse = OrderResponse.from(order, orderItemResponses)
        val expectedTotalPrice = orderItems.sumOf { it.menuPrice } + orderItemOptions.sumOf { it.optionPrice }

        webTestClient.get().uri("/api/v1/users/$userId/orders/${order.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.orderNumber").isEqualTo(expectedResponse.orderNumber)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId)
            .jsonPath("$.categoryId").isEqualTo(expectedResponse.categoryId)
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
            .jsonPath("$.totalPrice").isEqualTo(expectedTotalPrice)
    }

    @DisplayName("회원은 주문 목록을 조회 및 검색할 수 있다.")
    @Test
    fun getOrders() {
        val userId = 1L

        val now = LocalDateTime.now()
        val firstOrder = Order(
            id = 1L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = userId,
            storeId = 1L,
            categoryId = 1L,
            paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "")}",
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 20000,
            deliveryFee = 0,
            createdAt = now,
            updatedAt = now,
        )

        val secondOrder = Order(
            id = 2L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = userId,
            storeId = 1L,
            categoryId = 1L,
            paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "")}",
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 20000,
            deliveryFee = 0,
            createdAt = now.minusDays(1),
            updatedAt = now.minusDays(1),
        )

        val thirdOrder = Order(
            id = 3L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = userId,
            storeId = 3L,
            categoryId = 1L,
            paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "")}",
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 16000,
            deliveryFee = 0,
            createdAt = now.minusWeeks(1),
            updatedAt = now.minusWeeks(1),
        )

        val fourthOrder = Order(
            id = 4L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = userId,
            storeId = 4L,
            categoryId = 1L,
            paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "")}",
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.TAKE_OUT,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 18000,
            deliveryFee = 0,
            createdAt = now.minusWeeks(2),
            updatedAt = now.minusWeeks(2),
        )

        val fifthOrder = Order(
            id = 5L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = userId,
            storeId = 5L,
            categoryId = 1L,
            paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "")}",
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.SELF,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 14500,
            deliveryFee = 0,
            createdAt = now.minusWeeks(3),
            updatedAt = now.minusWeeks(3),
        )
        val orders = listOf(firstOrder, secondOrder, thirdOrder, fourthOrder, fifthOrder).sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(orders, pageable, orders.size.toLong())
        coEvery { useCase.getOrdersByApiCondition(any(), any()) } returns page

        val orderItems = listOf(
            OrderItem(1L, 1L, 1L, "피자", 15000, 1, now),
            OrderItem(2L, 1L, 2L, "불고기 버거", 5000, 1, now),
            OrderItem(3L, 2L, 1L, "피자", 15000, 1, now),
            OrderItem(4L, 2L, 2L, "불고기 버거", 5000, 1, now),
            OrderItem(5L, 3L, 5L, "아메리카노", 4000, 4, now),
            OrderItem(6L, 4L, 7L, "카페라떼", 5000, 3, now),
            OrderItem(7L, 4L, 13L, "스콘", 3000, 1, now),
            OrderItem(8L, 5L, 20L, "짜장면", 6000, 1, now),
            OrderItem(9L, 5L, 23L, "볶음밥", 8500, 1, now),
        )
        coEvery { orderItemUseCase.getAllByOrderIdIn(any()) } returns orderItems

        val stores = listOf(
            Store(
                id = 1L,
                categoryId = 1L,
                deliveryType = DeliveryType.OUTSOURCING,
                name = "BBQ",
                ownerName = "김성현",
                taxId = "123-12-12345",
                deliveryFee = 1000,
                minimumOrderAmount = 18000,
                iconImageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/icon-image-1.jpeg",
                description = "저희 업소는 100% 국내산 닭고기를 사용하며, BBQ 올리브 오일만을 사용합니다.",
                foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
                phoneNumber = "02-1234-1234",
                createdAt = now,
                updatedAt = now,
            ),
            Store(
                id = 3L,
                categoryId = 2L,
                deliveryType = DeliveryType.OUTSOURCING,
                name = "커피맛집",
                ownerName = "김커피",
                taxId = "125-21-09283",
                deliveryFee = 1000,
                minimumOrderAmount = 13000,
                iconImageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/icon-image-2.jpeg",
                description = "안녕하세요. 뺵보이피자입니다.",
                foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
                phoneNumber = "02-1938-2973",
                createdAt = now.plusHours(3),
                updatedAt = now.plusHours(3),
            ),
            Store(
                id = 4L,
                categoryId = 2L,
                deliveryType = DeliveryType.TAKE_OUT,
                name = "카페천국",
                ownerName = "나커피",
                taxId = "125-21-12397",
                deliveryFee = 3000,
                minimumOrderAmount = 15000,
                iconImageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/icon-image-3.jpeg",
                description = "안녕하세요. 카페천국입니다 :)",
                foodOrigin = "",
                phoneNumber = "02-1726-2397",
                createdAt = now.plusHours(4),
                updatedAt = now.plusHours(4),
            ),
            Store(
                id = 5L,
                categoryId = 2L,
                deliveryType = DeliveryType.SELF,
                name = "대림성",
                ownerName = "나짜장",
                taxId = "125-21-38723",
                deliveryFee = 2500,
                minimumOrderAmount = 16000,
                iconImageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/icon-image-4.jpeg",
                description = "안녕하세요. 대림성입니다 :)",
                foodOrigin = "",
                phoneNumber = "070-9278-8765",
                createdAt = now.plusHours(5),
                updatedAt = now.plusHours(5),
            )
        )
        coEvery { storeUseCase.getAllByIds(any()) } returns stores

        webTestClient.get().uri("/api/v1/users/$userId/orders?sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].id").isEqualTo(orders[0].id!!)
            .jsonPath("$.content[0].orderNumber").isEqualTo(orders[0].orderNumber)
            .jsonPath("$.content[0].userId").isEqualTo(orders[0].userId)
            .jsonPath("$.content[0].storeId").isEqualTo(orders[0].storeId)
            .jsonPath("$.content[0].categoryId").isEqualTo(orders[0].categoryId)
            .jsonPath("$.content[0].orderStatus").isEqualTo(orders[0].status.name)
            .jsonPath("$.content[0].deliveryType").isEqualTo(orders[0].deliveryType.name)
            .jsonPath("$.content[0].storeIconImageUrl").isEqualTo(stores[3].iconImageUrl!!)
            .jsonPath("$.content[0].menuName").isEqualTo("짜장면 외 1개")
            .jsonPath("$.content[0].totalPrice").isEqualTo(orders[0].totalPrice)
            .jsonPath("$.content[0].deliveryFee").isEqualTo(orders[0].deliveryFee)
            .jsonPath("$.content[0].createdAt").exists()

            .jsonPath("$.content[1].id").isEqualTo(orders[1].id!!)
            .jsonPath("$.content[1].orderNumber").isEqualTo(orders[1].orderNumber)
            .jsonPath("$.content[1].userId").isEqualTo(orders[1].userId)
            .jsonPath("$.content[1].storeId").isEqualTo(orders[1].storeId)
            .jsonPath("$.content[1].categoryId").isEqualTo(orders[1].categoryId)
            .jsonPath("$.content[1].orderStatus").isEqualTo(orders[1].status.name)
            .jsonPath("$.content[1].deliveryType").isEqualTo(orders[1].deliveryType.name)
            .jsonPath("$.content[1].storeIconImageUrl").isEqualTo(stores[2].iconImageUrl!!)
            .jsonPath("$.content[1].menuName").isEqualTo("카페라떼 외 1개")
            .jsonPath("$.content[1].totalPrice").isEqualTo(orders[1].totalPrice)
            .jsonPath("$.content[1].deliveryFee").isEqualTo(orders[1].deliveryFee)
            .jsonPath("$.content[1].createdAt").exists()

            .jsonPath("$.content[2].id").isEqualTo(orders[2].id!!)
            .jsonPath("$.content[2].orderNumber").isEqualTo(orders[2].orderNumber)
            .jsonPath("$.content[2].userId").isEqualTo(orders[2].userId)
            .jsonPath("$.content[2].storeId").isEqualTo(orders[2].storeId)
            .jsonPath("$.content[2].categoryId").isEqualTo(orders[2].categoryId)
            .jsonPath("$.content[2].orderStatus").isEqualTo(orders[2].status.name)
            .jsonPath("$.content[2].deliveryType").isEqualTo(orders[2].deliveryType.name)
            .jsonPath("$.content[2].storeIconImageUrl").isEqualTo(stores[1].iconImageUrl!!)
            .jsonPath("$.content[2].menuName").isEqualTo("아메리카노 4개")
            .jsonPath("$.content[2].totalPrice").isEqualTo(orders[2].totalPrice)
            .jsonPath("$.content[2].deliveryFee").isEqualTo(orders[2].deliveryFee)
            .jsonPath("$.content[2].createdAt").exists()

            .jsonPath("$.content[3].id").isEqualTo(orders[3].id!!)
            .jsonPath("$.content[3].orderNumber").isEqualTo(orders[3].orderNumber)
            .jsonPath("$.content[3].userId").isEqualTo(orders[3].userId)
            .jsonPath("$.content[3].storeId").isEqualTo(orders[3].storeId)
            .jsonPath("$.content[3].categoryId").isEqualTo(orders[3].categoryId)
            .jsonPath("$.content[3].orderStatus").isEqualTo(orders[3].status.name)
            .jsonPath("$.content[3].deliveryType").isEqualTo(orders[3].deliveryType.name)
            .jsonPath("$.content[3].storeIconImageUrl").isEqualTo(stores[0].iconImageUrl!!)
            .jsonPath("$.content[3].menuName").isEqualTo("피자 외 1개")
            .jsonPath("$.content[3].totalPrice").isEqualTo(orders[3].totalPrice)
            .jsonPath("$.content[3].deliveryFee").isEqualTo(orders[3].deliveryFee)
            .jsonPath("$.content[3].createdAt").exists()

            .jsonPath("$.content[4].id").isEqualTo(orders[4].id!!)
            .jsonPath("$.content[4].orderNumber").isEqualTo(orders[4].orderNumber)
            .jsonPath("$.content[4].userId").isEqualTo(orders[4].userId)
            .jsonPath("$.content[4].storeId").isEqualTo(orders[4].storeId)
            .jsonPath("$.content[4].categoryId").isEqualTo(orders[4].categoryId)
            .jsonPath("$.content[4].orderStatus").isEqualTo(orders[4].status.name)
            .jsonPath("$.content[4].deliveryType").isEqualTo(orders[4].deliveryType.name)
            .jsonPath("$.content[4].storeIconImageUrl").isEqualTo(stores[0].iconImageUrl!!)
            .jsonPath("$.content[4].menuName").isEqualTo("피자 외 1개")
            .jsonPath("$.content[4].totalPrice").isEqualTo(orders[4].totalPrice)
            .jsonPath("$.content[4].deliveryFee").isEqualTo(orders[4].deliveryFee)
            .jsonPath("$.content[4].createdAt").exists()

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

    private fun generateOrder(id: Long, userId: Long, request: CreateOrderRequest, now: LocalDateTime): Order {
        val orderNumber = generateOrderNumber(now)
        val paymentId = generatePaymentId()
        return Order(
            id = id,
            orderNumber = orderNumber,
            userId = userId,
            storeId = request.storeId,
            categoryId = request.categoryId,
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
