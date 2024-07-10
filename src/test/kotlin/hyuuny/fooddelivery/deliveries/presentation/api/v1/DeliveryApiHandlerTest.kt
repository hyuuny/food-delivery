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
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.users.domain.User
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class DeliveryApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: DeliveryUseCase

    @DisplayName("라이더는 배달을 수락할 수 있다.")
    @Test
    fun acceptDelivery() {
        val orderId = 1L
        val riderId = 293L

        val now = LocalDateTime.now()
        val order = Order(
            id = orderId,
            orderNumber = generateOrderNumber(now),
            userId = 95L,
            storeId = 1L,
            categoryId = 1L,
            paymentId = generatePaymentId(),
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CONFIRMED,
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

        val request = AcceptDeliveryRequest(
            orderId = order.id!!,
            riderId = rider.id!!,
        )
        val delivery = Delivery(
            id = 1L,
            orderId = orderId,
            riderId = riderId,
            status = DeliveryStatus.ACCEPTED,
            createdAt = now,
        )

        coEvery { useCase.acceptDelivery(any(), any(), any()) } returns delivery

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
            .jsonPath("$.status").isEqualTo(delivery.status.name)
            .jsonPath("$.pickupTime").doesNotExist()
            .jsonPath("$.deliveredTime").doesNotExist()
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

}
