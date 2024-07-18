package hyuuny.fooddelivery.orders.presentation.admin.v1

import ChangeOrderStatusRequest
import com.ninjasquad.springmockk.MockkBean
import generateOrderNumber
import generatePaymentId
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.orders.application.OrderItemOptionUseCase
import hyuuny.fooddelivery.orders.application.OrderItemUseCase
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.orders.domain.OrderItem
import hyuuny.fooddelivery.orders.domain.OrderItemOption
import hyuuny.fooddelivery.orders.presentation.admin.v1.response.OrderItemOptionResponse
import hyuuny.fooddelivery.orders.presentation.admin.v1.response.OrderItemResponse
import hyuuny.fooddelivery.orders.presentation.admin.v1.response.OrderResponse
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
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

class OrderHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: OrderUseCase

    @MockkBean
    private lateinit var orderItemUseCase: OrderItemUseCase

    @MockkBean
    private lateinit var orderItemOptionUseCase: OrderItemOptionUseCase

    @MockkBean
    private lateinit var userUseCase: UserUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

    @DisplayName("관리자는 회원들의 주문 목록을 조회할 수 있다.")
    @Test
    fun getOrders() {
        val now = LocalDateTime.now()
        val firstOrder = Order(
            id = 1L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = 1L,
            storeId = 1L,
            categoryId = 1L,
            couponId = null,
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
            orderPrice = 20000,
            couponDiscountAmount = 0,
            totalPrice = 20000,
            deliveryFee = 0,
            createdAt = now,
            updatedAt = now,
        )

        val secondOrder = Order(
            id = 2L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = 2L,
            storeId = 1L,
            categoryId = 1L,
            couponId = null,
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
            orderPrice = 20000,
            couponDiscountAmount = 0,
            totalPrice = 20000,
            deliveryFee = 0,
            createdAt = now.minusDays(1),
            updatedAt = now.minusDays(1),
        )

        val thirdOrder = Order(
            id = 3L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = 3L,
            storeId = 3L,
            categoryId = 1L,
            couponId = null,
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
            orderPrice = 16000,
            couponDiscountAmount = 0,
            totalPrice = 16000,
            deliveryFee = 0,
            createdAt = now.minusWeeks(1),
            updatedAt = now.minusWeeks(1),
        )

        val fourthOrder = Order(
            id = 4L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = 4L,
            storeId = 4L,
            categoryId = 1L,
            couponId = null,
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
            orderPrice = 18000,
            couponDiscountAmount = 0,
            totalPrice = 18000,
            deliveryFee = 0,
            createdAt = now.minusWeeks(2),
            updatedAt = now.minusWeeks(2),
        )

        val fifthOrder = Order(
            id = 5L,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = 5L,
            storeId = 5L,
            categoryId = 1L,
            couponId = null,
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
            orderPrice = 14500,
            couponDiscountAmount = 0,
            totalPrice = 14500,
            deliveryFee = 0,
            createdAt = now.minusWeeks(3),
            updatedAt = now.minusWeeks(3),
        )
        val orders = listOf(firstOrder, secondOrder, thirdOrder, fourthOrder, fifthOrder).sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(orders, pageable, orders.size.toLong())
        coEvery { useCase.getOrderByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/orders?sort=id:desc")
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
            .jsonPath("$.content[0].paymentId").isEqualTo(orders[0].paymentId)
            .jsonPath("$.content[0].paymentMethod").isEqualTo(orders[0].paymentMethod.name)
            .jsonPath("$.content[0].status").isEqualTo(orders[0].status.name)
            .jsonPath("$.content[0].deliveryType").isEqualTo(orders[0].deliveryType.name)
            .jsonPath("$.content[0].phoneNumber").isEqualTo(orders[0].phoneNumber)
            .jsonPath("$.content[0].createdAt").exists()

            .jsonPath("$.content[1].id").isEqualTo(orders[1].id!!)
            .jsonPath("$.content[1].orderNumber").isEqualTo(orders[1].orderNumber)
            .jsonPath("$.content[1].userId").isEqualTo(orders[1].userId)
            .jsonPath("$.content[1].storeId").isEqualTo(orders[1].storeId)
            .jsonPath("$.content[1].categoryId").isEqualTo(orders[1].categoryId)
            .jsonPath("$.content[1].paymentId").isEqualTo(orders[1].paymentId)
            .jsonPath("$.content[1].paymentMethod").isEqualTo(orders[1].paymentMethod.name)
            .jsonPath("$.content[1].status").isEqualTo(orders[1].status.name)
            .jsonPath("$.content[1].deliveryType").isEqualTo(orders[1].deliveryType.name)
            .jsonPath("$.content[1].phoneNumber").isEqualTo(orders[1].phoneNumber)
            .jsonPath("$.content[1].createdAt").exists()

            .jsonPath("$.content[2].id").isEqualTo(orders[2].id!!)
            .jsonPath("$.content[2].orderNumber").isEqualTo(orders[2].orderNumber)
            .jsonPath("$.content[2].userId").isEqualTo(orders[2].userId)
            .jsonPath("$.content[2].storeId").isEqualTo(orders[2].storeId)
            .jsonPath("$.content[2].categoryId").isEqualTo(orders[2].categoryId)
            .jsonPath("$.content[2].paymentId").isEqualTo(orders[2].paymentId)
            .jsonPath("$.content[2].paymentMethod").isEqualTo(orders[2].paymentMethod.name)
            .jsonPath("$.content[2].status").isEqualTo(orders[2].status.name)
            .jsonPath("$.content[2].phoneNumber").isEqualTo(orders[2].phoneNumber)
            .jsonPath("$.content[2].createdAt").exists()

            .jsonPath("$.content[3].id").isEqualTo(orders[3].id!!)
            .jsonPath("$.content[3].orderNumber").isEqualTo(orders[3].orderNumber)
            .jsonPath("$.content[3].userId").isEqualTo(orders[3].userId)
            .jsonPath("$.content[3].storeId").isEqualTo(orders[3].storeId)
            .jsonPath("$.content[3].categoryId").isEqualTo(orders[3].categoryId)
            .jsonPath("$.content[3].paymentId").isEqualTo(orders[3].paymentId)
            .jsonPath("$.content[3].paymentMethod").isEqualTo(orders[3].paymentMethod.name)
            .jsonPath("$.content[3].status").isEqualTo(orders[3].status.name)
            .jsonPath("$.content[3].phoneNumber").isEqualTo(orders[3].phoneNumber)
            .jsonPath("$.content[3].createdAt").exists()

            .jsonPath("$.content[4].id").isEqualTo(orders[4].id!!)
            .jsonPath("$.content[4].orderNumber").isEqualTo(orders[4].orderNumber)
            .jsonPath("$.content[4].userId").isEqualTo(orders[4].userId)
            .jsonPath("$.content[4].storeId").isEqualTo(orders[4].storeId)
            .jsonPath("$.content[4].categoryId").isEqualTo(orders[4].categoryId)
            .jsonPath("$.content[4].paymentId").isEqualTo(orders[4].paymentId)
            .jsonPath("$.content[4].paymentMethod").isEqualTo(orders[4].paymentMethod.name)
            .jsonPath("$.content[4].status").isEqualTo(orders[4].status.name)
            .jsonPath("$.content[4].phoneNumber").isEqualTo(orders[4].phoneNumber)
            .jsonPath("$.content[4].createdAt").exists()

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

    @DisplayName("관리자는 회원의 주문 내역을 상세조회 할 수 있다.")
    @Test
    fun getOrder() {
        val id = 57L
        val userId = 1L
        val storeId = 7L
        val categoryId = 13L
        val couponId = 27L

        val now = LocalDateTime.now()
        val user = User(
            id = userId,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )
        val store = Store(
            id = storeId,
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며, BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            createdAt = now,
            updatedAt = now,
        )
        val couponDiscountAmount = 5000L
        val order = Order(
            id = id,
            orderNumber = generateOrderNumber(now),
            userId = userId,
            storeId = storeId,
            categoryId = categoryId,
            couponId = couponId,
            paymentId = generatePaymentId(),
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = "리뷰이벤트 참여합니다 !",
            orderPrice = 23000,
            couponDiscountAmount = couponDiscountAmount,
            totalPrice = 23000 - couponDiscountAmount,
            deliveryFee = 0,
            createdAt = now,
            updatedAt = now,
        )
        val orderItems = listOf(
            OrderItem(1L, 1L, 1L, "피자", 15000, 1, now),
            OrderItem(2L, 1L, 2L, "불고기 버거", 5000, 1, now),
        )
        val orderItemOptions = listOf(
            OrderItemOption(1L, 1L, 1L, "페퍼로니", 1000, now),
            OrderItemOption(2L, 1L, 2L, "제로콜라", 1500, now),
            OrderItemOption(3L, 2L, 3L, "콘 아이스크림", 500, now),
        )

        coEvery { useCase.getOrder(any()) } returns order
        coEvery { orderItemUseCase.getAllByOrderId(any()) } returns orderItems
        coEvery { orderItemOptionUseCase.getAllByOrderItemIdIn(any()) } returns orderItemOptions
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { storeUseCase.getStore(any()) } returns store

        val orderItemResponses = orderItems.map { orderItem ->
            val itemOptions = orderItemOptions.filter { it.orderItemId == orderItem.id }
                .map { OrderItemOptionResponse.from(it) }
            OrderItemResponse.from(orderItem, itemOptions)
        }
        val expectedResponse = OrderResponse.from(order, user.name, store.name, orderItemResponses)

        webTestClient.get().uri("/admin/v1/orders/${order.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.orderNumber").isEqualTo(expectedResponse.orderNumber)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.userName").isEqualTo(expectedResponse.userName)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId)
            .jsonPath("$.storeName").isEqualTo(expectedResponse.storeName)
            .jsonPath("$.categoryId").isEqualTo(expectedResponse.categoryId)
            .jsonPath("$.couponId").isEqualTo(expectedResponse.couponId!!)
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
            .jsonPath("$.orderPrice").isEqualTo(expectedResponse.orderPrice)
            .jsonPath("$.couponDiscountAmount").isEqualTo(expectedResponse.couponDiscountAmount)
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
    }

    @DisplayName("관리자는 회원의 주문 상태를 변경할 수 있다.")
    @Test
    fun changeOrderStatus() {
        val id = 1L
        val request = ChangeOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY)

        coEvery { useCase.changeOrderStatus(any(), any()) } returns Unit

        webTestClient.patch().uri("/admin/v1/orders/$id/change-order-status")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.message").isEqualTo(
                "${id}번 주문이 '${OrderStatus.OUT_FOR_DELIVERY.value}' 상태로 변경되었습니다."
            )
    }

}
