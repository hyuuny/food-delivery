package hyuuny.fooddelivery.deliveries.application

import generateOrderNumber
import generatePaymentId
import hyuuny.fooddelivery.common.constant.*
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.deliveries.infrastructure.DeliveryRepository
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

class DeliveredUseCaseTest : BehaviorSpec({

    val repository = mockk<DeliveryRepository>()
    val useCase = DeliveryUseCase(repository)
    val orderUseCase = mockk<OrderUseCase>()
    val userUseCase = mockk<UserUseCase>()
    val verifier = mockk<DeliveryVerifier>()

    Given("라이더가 음식을") {
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

        val delivery = Delivery(
            id = 1L,
            orderId = orderId,
            riderId = riderId,
            status = DeliveryStatus.ACCEPTED,
            createdAt = now,
        )

        coEvery { orderUseCase.getOrder(any()) } returns order
        coEvery { userUseCase.getUser(any()) } returns rider
        coEvery { repository.findById(any()) } returns delivery
        coEvery { repository.updateDeliveredTime(any()) } returns Unit

        When("정상적으로 고객에게 전달했으면") {
            useCase.delivered(delivery.id!!, { order }, { rider })

            Then("배달 완료할 수 있다.") {
                coVerify { repository.updateDeliveredTime(any()) }
            }
        }

        When("존재하지 않는 배달이면") {
            coEvery { repository.findById(any()) } returns null

            Then("배달을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.delivered(0, { order }, { rider })
                }
                ex.message shouldBe "0번 배달 내역을 찾을 수 없습니다."
            }
        }

        When("주문 정보가 일치하지 않으면") {
            val otherOrder = Order(
                id = 2L,
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
            coEvery { orderUseCase.getOrder(any()) } returns otherOrder
            coEvery { repository.findById(any()) } returns delivery
            coEvery { verifier.verifyDelivered(any(), any(), any()) } throws IllegalArgumentException("주문 정보가 일치하지 않습니다.")

            Then("주문 정보가 일치하지 않는다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.delivered(delivery.id!!, { otherOrder }, { rider })
                }
                ex.message shouldBe "주문 정보가 일치하지 않습니다."
            }
        }

        When("라이더 정보가 일치하지 않으면") {
            val invalidDelivery = Delivery(
                id = 1L,
                orderId = orderId,
                riderId = 5L,
                status = DeliveryStatus.ACCEPTED,
                createdAt = now,
            )
            coEvery { repository.findById(any()) } returns invalidDelivery

            Then("라이더 정보가 일치하지 않는다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.pickup(invalidDelivery.id!!, { order }, { rider })
                }
                ex.message shouldBe "라이더 정보가 일치하지 않습니다."
            }
        }

        When("이미 취소된 배달이면") {
            val invalidDelivery = Delivery(
                id = 1L,
                orderId = orderId,
                riderId = riderId,
                status = DeliveryStatus.ACCEPTED,
                cancelTime = now,
                createdAt = now,
            )
            coEvery { repository.findById(any()) } returns invalidDelivery
            coEvery { verifier.verifyDelivered(any(), any(), any()) } throws IllegalArgumentException("이미 취소된 배달입니다.")

            Then("이미 취소된 배달이라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.delivered(invalidDelivery.id!!, { order }, { rider })
                }
                ex.message shouldBe "이미 취소된 배달입니다."
            }
        }

        When("이미 완료된 배달이면") {
            val invalidDelivery = Delivery(
                id = 1L,
                orderId = orderId,
                riderId = riderId,
                status = DeliveryStatus.DELIVERED,
                cancelTime = null,
                deliveredTime = now,
                createdAt = now,
            )
            coEvery { repository.findById(any()) } returns invalidDelivery
            coEvery { verifier.verifyDelivered(any(), any(), any()) } throws IllegalArgumentException("이미 완료된 배달입니다.")

            Then("이미 완료된 배달이라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.delivered(invalidDelivery.id!!, { order }, { rider })
                }
                ex.message shouldBe "이미 완료된 배달입니다."
            }
        }

    }

})
