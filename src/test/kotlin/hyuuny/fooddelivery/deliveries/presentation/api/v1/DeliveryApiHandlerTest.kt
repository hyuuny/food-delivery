package hyuuny.fooddelivery.deliveries.presentation.api.v1

import AcceptDeliveryRequest
import CancelDeliveryRequest
import PickupDeliveryRequest
import com.ninjasquad.springmockk.MockkBean
import generateOrderNumber
import generatePaymentId
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.*
import hyuuny.fooddelivery.deliveries.application.DeliveryUseCase
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.stores.application.StoreDetailUseCase
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.domain.StoreDetail
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

class DeliveryApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: DeliveryUseCase

    @MockkBean
    private lateinit var userUseCase: UserUseCase

    @MockkBean
    private lateinit var orderUseCase: OrderUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

    @MockkBean
    private lateinit var storeDetailUseCase: StoreDetailUseCase

    @DisplayName("라이더는 배달을 수락할 수 있다.")
    @Test
    fun acceptDelivery() {
        val orderId = 1L
        val riderId = 293L
        val storeId = 5L
        val userId = 95L

        val now = LocalDateTime.now()
        val order = Order(
            id = orderId,
            orderNumber = generateOrderNumber(now),
            userId = userId,
            storeId = storeId,
            categoryId = 1L,
            paymentId = generatePaymentId(),
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.DELIVERY_COMPLETED,
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

        val rider = User(
            id = riderId,
            userType = UserType.RIDER,
            name = "라이더",
            nickname = "적토마",
            email = "rider123@knou.ac.kr",
            phoneNumber = "010-8392-1280",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/rider-start.jpeg",
            createdAt = now,
            updatedAt = now,
        )

        val store = Store(
            id = storeId,
            categoryId = 2L,
            deliveryType = DeliveryType.TAKE_OUT,
            name = "카페천국",
            ownerName = "나커피",
            taxId = "125-21-12397",
            deliveryFee = 3000,
            minimumOrderAmount = 15000,
            iconImageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/icon-image-3.jpeg",
            description = "안녕하세요. 카페천국입니다 :)",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "02-1726-2397",
            createdAt = now.plusHours(4),
            updatedAt = now.plusHours(4),
        )
        val storeDetail = StoreDetail(
            id = 1,
            storeId = store.id!!,
            zipCode = "12345",
            address = "서울시 강남구 강남대로123길 12",
            detailedAddress = "1층 101호",
            openHours = "매일 오전 11:00 ~ 오후 11시 30분",
            closedDay = null,
            createdAt = now,
        )

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

        val request = AcceptDeliveryRequest(
            orderId = order.id!!,
            riderId = rider.id!!,
        )
        val delivery = Delivery(
            id = 1L,
            orderId = orderId,
            riderId = riderId,
            status = DeliveryStatus.DELIVERED,
            pickupTime = now.minusDays(1),
            deliveredTime = now.minusDays(1),
            createdAt = now,
        )

        coEvery { useCase.acceptDelivery(any(), any(), any()) } returns delivery
        coEvery { orderUseCase.getOrder(any()) } returns order
        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { storeDetailUseCase.getStoreDetailByStoreId(any()) } returns storeDetail
        coEvery { userUseCase.getUser(any()) } returns user

        webTestClient.post().uri("/api/v1/deliveries/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(delivery.id!!)
            .jsonPath("$.riderId").isEqualTo(delivery.riderId)
            .jsonPath("$.orderId").isEqualTo(delivery.orderId)
            .jsonPath("$.orderNumber").isEqualTo(order.orderNumber)
            .jsonPath("$.storeName").isEqualTo(store.name)
            .jsonPath("$.storeZipCode").isEqualTo(storeDetail.zipCode)
            .jsonPath("$.storeAddress").isEqualTo(storeDetail.address)
            .jsonPath("$.storeDetailAddress").isEqualTo(storeDetail.detailedAddress!!)
            .jsonPath("$.userName").isEqualTo(user.name)
            .jsonPath("$.userZipCode").isEqualTo(order.zipCode)
            .jsonPath("$.userAddress").isEqualTo(order.address)
            .jsonPath("$.userDetailAddress").isEqualTo(order.detailAddress)
            .jsonPath("$.messageToRider").isEqualTo(order.messageToRider!!)
            .jsonPath("$.totalPrice").isEqualTo(order.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(order.deliveryFee)
            .jsonPath("$.status").isEqualTo(delivery.status.name)
            .jsonPath("$.pickupTime").exists()
            .jsonPath("$.deliveredTime").exists()
            .jsonPath("$.cancelTime").doesNotExist()
            .jsonPath("$.createdAt").exists()
    }

    @DisplayName("배달을 취소할 수 있다.")
    @Test
    fun cancel() {
        val id = 1L
        val orderId = 1L
        val riderId = 293L

        val request = CancelDeliveryRequest(
            orderId = orderId,
            riderId = riderId,
        )

        coEvery { useCase.cancel(any(), any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/deliveries/$id/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("음식을 픽업할 수 있다.")
    @Test
    fun pickup() {
        val id = 1L
        val orderId = 38L
        val riderId = 293L

        val request = PickupDeliveryRequest(
            orderId = orderId,
            riderId = riderId
        )
        coEvery { useCase.pickup(any(), any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/deliveries/$id/pickup")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("배달 완료 처리할 수 있다.")
    @Test
    fun delivered() {
        val id = 1L
        val orderId = 38L
        val riderId = 293L

        val request = PickupDeliveryRequest(
            orderId = orderId,
            riderId = riderId
        )
        coEvery { useCase.delivered(any(), any(), any()) } returns Unit

        webTestClient.patch().uri("/api/v1/deliveries/$id/delivered")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("라이더는 자신의 배달 내역 목록을 조회할 수 있다.")
    @Test
    fun getDeliveries() {
        val riderId = 293L
        val ids = listOf(23L, 94L, 293L, 299L, 590L)
        val orderIds = listOf(34L, 234L, 430L, 2830L, 4810L)
        val storeIds = listOf(98L, 349L, 550L, 789L, 989L)

        val now = LocalDateTime.now()
        val rider = User(
            id = riderId,
            userType = UserType.RIDER,
            name = "라이더",
            nickname = "적토마",
            email = "rider123@knou.ac.kr",
            phoneNumber = "010-8392-1280",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/rider-start.jpeg",
            createdAt = now,
            updatedAt = now,
        )

        val deliveries = ids.mapIndexed { idx, it ->
            Delivery(
                id = it,
                riderId = riderId,
                orderId = orderIds[idx],
                status = DeliveryStatus.DELIVERED,
                createdAt = now
            )
        }

        val orders = orderIds.mapIndexed { idx, it ->
            Order(
                id = orderIds[idx],
                orderNumber = generateOrderNumber(now),
                userId = 95L,
                storeId = storeIds[idx],
                categoryId = 1L,
                paymentId = generatePaymentId(),
                paymentMethod = PaymentMethod.NAVER_PAY,
                status = OrderStatus.DELIVERY_COMPLETED,
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
        }

        val stores = storeIds.mapIndexed { idx, it ->
            Store(
                id = it,
                categoryId = 2L,
                deliveryType = DeliveryType.OUTSOURCING,
                name = "${it}번가 피자",
                ownerName = "나피자",
                taxId = "125-21-3892${idx}",
                deliveryFee = 0,
                minimumOrderAmount = (idx + 1) * 1000L,
                iconImageUrl = "icon-image-url-1.jpg",
                description = "안녕하세요!",
                foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
                phoneNumber = "02-1231-2308",
                createdAt = now,
                updatedAt = now,
            )
        }
        val sortedDeliveries = deliveries.sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(sortedDeliveries, pageable, sortedDeliveries.size.toLong())
        coEvery { useCase.getDeliveriesByApiCondition(any(), any()) } returns page
        coEvery { userUseCase.getUser(any()) } returns rider
        coEvery { orderUseCase.getAllByIds(any()) } returns orders
        coEvery { storeUseCase.getAllByIds(any()) } returns stores

        webTestClient.get().uri("/api/v1/users/$riderId/deliveries")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.riderId").isEqualTo(riderId)
            .jsonPath("$.riderName").isEqualTo(rider.name)
            .jsonPath("$.details.content[0].id").isEqualTo(deliveries[4].id!!)
            .jsonPath("$.details.content[0].orderId").isEqualTo(orders[4].id!!)
            .jsonPath("$.details.content[0].storeName").isEqualTo(stores[4].name)
            .jsonPath("$.details.content[0].phoneNumber").isEqualTo(orders[4].phoneNumber)
            .jsonPath("$.details.content[0].totalPrice").isEqualTo(orders[4].totalPrice)
            .jsonPath("$.details.content[0].deliveryFee").isEqualTo(orders[4].deliveryFee)

            .jsonPath("$.details.content[1].id").isEqualTo(deliveries[3].id!!)
            .jsonPath("$.details.content[1].orderId").isEqualTo(orders[3].id!!)
            .jsonPath("$.details.content[1].storeName").isEqualTo(stores[3].name)
            .jsonPath("$.details.content[1].phoneNumber").isEqualTo(orders[3].phoneNumber)
            .jsonPath("$.details.content[1].totalPrice").isEqualTo(orders[3].totalPrice)
            .jsonPath("$.details.content[1].deliveryFee").isEqualTo(orders[3].deliveryFee)

            .jsonPath("$.details.content[2].id").isEqualTo(deliveries[2].id!!)
            .jsonPath("$.details.content[2].orderId").isEqualTo(orders[2].id!!)
            .jsonPath("$.details.content[2].storeName").isEqualTo(stores[2].name)
            .jsonPath("$.details.content[2].phoneNumber").isEqualTo(orders[2].phoneNumber)
            .jsonPath("$.details.content[2].totalPrice").isEqualTo(orders[2].totalPrice)
            .jsonPath("$.details.content[2].deliveryFee").isEqualTo(orders[2].deliveryFee)

            .jsonPath("$.details.content[3].id").isEqualTo(deliveries[1].id!!)
            .jsonPath("$.details.content[3].orderId").isEqualTo(orders[1].id!!)
            .jsonPath("$.details.content[3].storeName").isEqualTo(stores[1].name)
            .jsonPath("$.details.content[3].phoneNumber").isEqualTo(orders[1].phoneNumber)
            .jsonPath("$.details.content[3].totalPrice").isEqualTo(orders[1].totalPrice)
            .jsonPath("$.details.content[3].deliveryFee").isEqualTo(orders[1].deliveryFee)

            .jsonPath("$.details.content[4].id").isEqualTo(deliveries[0].id!!)
            .jsonPath("$.details.content[4].orderId").isEqualTo(orders[0].id!!)
            .jsonPath("$.details.content[4].storeName").isEqualTo(stores[0].name)
            .jsonPath("$.details.content[4].phoneNumber").isEqualTo(orders[0].phoneNumber)
            .jsonPath("$.details.content[4].totalPrice").isEqualTo(orders[0].totalPrice)
            .jsonPath("$.details.content[4].deliveryFee").isEqualTo(orders[0].deliveryFee)

            .jsonPath("$.details.pageNumber").isEqualTo(1)
            .jsonPath("$.details.size").isEqualTo(15)
            .jsonPath("$.details.last").isEqualTo(true)
            .jsonPath("$.details.totalElements").isEqualTo(5)
    }

}
