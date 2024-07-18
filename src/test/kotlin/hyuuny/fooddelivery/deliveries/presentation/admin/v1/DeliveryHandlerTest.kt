package hyuuny.fooddelivery.deliveries.presentation.admin.v1

import ChangeDeliveryStatusRequest
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

class DeliveryHandlerTest : BaseIntegrationTest() {

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

    @DisplayName("배달 목록을 조회할 수 있다.")
    @Test
    fun getDeliveries() {
        val ids = listOf(23L, 94L, 293L, 299L, 590L)
        val riderIds = listOf(293L, 299L, 320L, 450L, 899L)
        val userIds = listOf(693L, 987L, 1029L, 2987L, 4273L)
        val orderIds = listOf(34L, 234L, 430L, 2830L, 4810L)
        val storeIds = listOf(98L, 349L, 550L, 789L, 989L)

        val now = LocalDateTime.now()
        val deliveries = ids.mapIndexed { idx, it ->
            Delivery(
                id = it,
                riderId = riderIds[idx],
                orderId = orderIds[idx],
                status = DeliveryStatus.DELIVERED,
                createdAt = now
            )
        }

        val riders = riderIds.mapIndexed { idx, it ->
            User(
                id = it,
                userType = UserType.RIDER,
                name = "라이더${idx}",
                nickname = "${idx}번 적토마",
                email = "rider12${idx}@knou.ac.kr",
                phoneNumber = "010-8392-128${idx}",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/rider-start.jpeg",
                createdAt = now,
                updatedAt = now,
            )
        }

        val users = userIds.mapIndexed { idx, it ->
            User(
                id = it,
                userType = UserType.CUSTOMER,
                name = "유저${idx}",
                nickname = "${idx}번 유저",
                email = "user12${idx}@knou.ac.kr",
                phoneNumber = "010-1234-567${idx}",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/user-start.jpeg",
                createdAt = now,
                updatedAt = now,
            )
        }

        val orders = orderIds.mapIndexed { idx, it ->
            Order(
                id = orderIds[idx],
                orderNumber = generateOrderNumber(LocalDateTime.now()),
                userId = userIds[idx],
                storeId = storeIds[idx],
                categoryId = 1L,
                couponId = null,
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
                orderPrice = 20000,
                couponDiscountAmount = 0,
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
        coEvery { useCase.getDeliveriesByAdminCondition(any(), any()) } returns page
        coEvery { userUseCase.getAllByIds(any()) } returns riders andThen users
        coEvery { orderUseCase.getAllByIds(any()) } returns orders
        coEvery { storeUseCase.getAllByIds(any()) } returns stores

        webTestClient.get().uri("/admin/v1/deliveries")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(deliveries[4].id!!)
            .jsonPath("$.content[0].riderId").isEqualTo(riders[4].id!!)
            .jsonPath("$.content[0].orderId").isEqualTo(orders[4].id!!)
            .jsonPath("$.content[0].orderNumber").isEqualTo(orders[4].orderNumber)
            .jsonPath("$.content[0].riderName").isEqualTo(riders[4].name)
            .jsonPath("$.content[0].storeId").isEqualTo(stores[4].id!!)
            .jsonPath("$.content[0].storeName").isEqualTo(stores[4].name)
            .jsonPath("$.content[0].userId").isEqualTo(users[4].id!!)
            .jsonPath("$.content[0].userName").isEqualTo(users[4].name)
            .jsonPath("$.content[0].status").isEqualTo(deliveries[4].status.name)

            .jsonPath("$.content[1].id").isEqualTo(deliveries[3].id!!)
            .jsonPath("$.content[1].riderId").isEqualTo(riders[3].id!!)
            .jsonPath("$.content[1].orderId").isEqualTo(orders[3].id!!)
            .jsonPath("$.content[1].orderNumber").isEqualTo(orders[3].orderNumber)
            .jsonPath("$.content[1].riderName").isEqualTo(riders[3].name)
            .jsonPath("$.content[1].storeId").isEqualTo(stores[3].id!!)
            .jsonPath("$.content[1].storeName").isEqualTo(stores[3].name)
            .jsonPath("$.content[1].userId").isEqualTo(users[3].id!!)
            .jsonPath("$.content[1].userName").isEqualTo(users[3].name)
            .jsonPath("$.content[1].status").isEqualTo(deliveries[3].status.name)

            .jsonPath("$.content[2].id").isEqualTo(deliveries[2].id!!)
            .jsonPath("$.content[2].riderId").isEqualTo(riders[2].id!!)
            .jsonPath("$.content[2].orderId").isEqualTo(orders[2].id!!)
            .jsonPath("$.content[2].orderNumber").isEqualTo(orders[2].orderNumber)
            .jsonPath("$.content[2].riderName").isEqualTo(riders[2].name)
            .jsonPath("$.content[2].storeId").isEqualTo(stores[2].id!!)
            .jsonPath("$.content[2].storeName").isEqualTo(stores[2].name)
            .jsonPath("$.content[2].userId").isEqualTo(users[2].id!!)
            .jsonPath("$.content[2].userName").isEqualTo(users[2].name)
            .jsonPath("$.content[2].status").isEqualTo(deliveries[2].status.name)

            .jsonPath("$.content[3].id").isEqualTo(deliveries[1].id!!)
            .jsonPath("$.content[3].riderId").isEqualTo(riders[1].id!!)
            .jsonPath("$.content[3].orderId").isEqualTo(orders[1].id!!)
            .jsonPath("$.content[3].orderNumber").isEqualTo(orders[1].orderNumber)
            .jsonPath("$.content[3].riderName").isEqualTo(riders[1].name)
            .jsonPath("$.content[3].storeId").isEqualTo(stores[1].id!!)
            .jsonPath("$.content[3].storeName").isEqualTo(stores[1].name)
            .jsonPath("$.content[3].userId").isEqualTo(users[1].id!!)
            .jsonPath("$.content[3].userName").isEqualTo(users[1].name)
            .jsonPath("$.content[3].status").isEqualTo(deliveries[1].status.name)

            .jsonPath("$.content[4].id").isEqualTo(deliveries[0].id!!)
            .jsonPath("$.content[4].riderId").isEqualTo(riders[0].id!!)
            .jsonPath("$.content[4].orderId").isEqualTo(orders[0].id!!)
            .jsonPath("$.content[4].orderNumber").isEqualTo(orders[0].orderNumber)
            .jsonPath("$.content[4].riderName").isEqualTo(riders[0].name)
            .jsonPath("$.content[4].storeId").isEqualTo(stores[0].id!!)
            .jsonPath("$.content[4].storeName").isEqualTo(stores[0].name)
            .jsonPath("$.content[4].userId").isEqualTo(users[0].id!!)
            .jsonPath("$.content[4].userName").isEqualTo(users[0].name)
            .jsonPath("$.content[4].status").isEqualTo(deliveries[0].status.name)

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

    @DisplayName("배달 상세 정보를 조회할 수 있다.")
    @Test
    fun getDelivery() {
        val id = 75L
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
            couponId = null,
            paymentId = generatePaymentId(),
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.OUT_FOR_DELIVERY,
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
            foodOrigin = "",
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

        val rider = User(
            id = riderId,
            userType = UserType.RIDER,
            name = "라이더",
            nickname = "적토마",
            email = "rider128@knou.ac.kr",
            phoneNumber = "010-8392-1280",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/rider-start.jpeg",
            createdAt = now,
            updatedAt = now,
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

        val delivery = Delivery(
            id = id,
            orderId = orderId,
            riderId = riderId,
            status = DeliveryStatus.DELIVERING,
            pickupTime = now.minusMinutes(20),
            createdAt = now,
        )

        coEvery { useCase.getDelivery(any()) } returns delivery
        coEvery { orderUseCase.getOrder(any()) } returns order
        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { storeDetailUseCase.getStoreDetailByStoreId(any()) } returns storeDetail
        coEvery { userUseCase.getUser(any()) } returns rider andThen user

        webTestClient.get().uri("/admin/v1/deliveries/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(delivery.id!!)
            .jsonPath("$.riderId").isEqualTo(delivery.riderId)
            .jsonPath("$.riderName").isEqualTo(rider.name)
            .jsonPath("$.riderPhoneNumber").isEqualTo(rider.phoneNumber)
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
            .jsonPath("$.deliveredTime").doesNotExist()
            .jsonPath("$.cancelTime").doesNotExist()
            .jsonPath("$.createdAt").exists()
    }

    @DisplayName("배달 상태를 변경할 수 있다.")
    @Test
    fun changeDeliveryStatus() {
        val id = 75L

        val request = ChangeDeliveryStatusRequest(
            status = DeliveryStatus.DELIVERED
        )
        coEvery { useCase.changeDeliverStatus(any(), any()) } returns Unit

        webTestClient.patch().uri("/admin/v1/deliveries/$id/change-deliver-status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

}
