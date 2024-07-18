package hyuuny.fooddelivery.deliveries.application

import AcceptDeliveryRequest
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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class AcceptDeliveryUseCaseTest : BehaviorSpec({

    val repository = mockk<DeliveryRepository>()
    val useCase = DeliveryUseCase(repository)
    val orderUseCase = mockk<OrderUseCase>()
    val userUseCase = mockk<UserUseCase>()
    val verifier = mockk<DeliveryVerifier>()

    Given("라이더가 배달 수락시에") {
        val orderId = 1L
        val riderId = 293L

        val now = LocalDateTime.now()
        val order = Order(
            id = orderId,
            orderNumber = generateOrderNumber(now),
            userId = 95L,
            storeId = 1L,
            categoryId = 1L,
            couponId = null,
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
            orderPrice = 20000,
            couponDiscountAmount = 0,
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

        coEvery { orderUseCase.getOrder(any()) } returns order
        coEvery { userUseCase.getUser(any()) } returns rider
        coEvery { repository.insert(any()) } returns delivery

        When("배달 수락 가능한 상태이면") {
            val result = useCase.acceptDelivery(
                request = request,
                getOrder = { order },
                getRider = { rider }
            )

            Then("배달을 수락할 수 있다.") {
                result.id.shouldNotBeNull()
                result.riderId shouldBe request.riderId
                result.orderId shouldBe request.orderId
                result.status shouldBe DeliveryStatus.ACCEPTED
                result.pickupTime.shouldBeNull()
                result.deliveredTime.shouldBeNull()
                result.cancelTime.shouldBeNull()
                result.createdAt.shouldNotBeNull()
            }
        }

        When("배달 가능한 주문 상태가 아니면") {
            val invalidOrder = Order(
                id = orderId,
                orderNumber = generateOrderNumber(now),
                userId = 95L,
                storeId = 1L,
                categoryId = 1L,
                couponId = null,
                paymentId = generatePaymentId(),
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
            coEvery { orderUseCase.getOrder(any()) } returns invalidOrder
            coEvery { verifier.verifyAccept(any(), any()) } throws IllegalArgumentException("배달 가능한 주문 상태가 아닙니다.")

            Then("배달을 수락할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.acceptDelivery(
                        request = request,
                        getOrder = { invalidOrder },
                        getRider = { rider }
                    )
                }
                ex.message shouldBe "배달 가능한 주문 상태가 아닙니다."
            }
        }

        When("라이더 회원이 아니면") {
            val invalidUser = User(
                id = 2834L,
                name = "김성현",
                nickname = "hyuuny",
                email = "shyune@knou.ac.kr",
                phoneNumber = "010-1234-1234",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
                createdAt = now,
                updatedAt = now,
            )
            coEvery { orderUseCase.getOrder(any()) } returns order
            coEvery { userUseCase.getUser(any()) } returns invalidUser
            coEvery { verifier.verifyAccept(any(), any()) } throws IllegalArgumentException("라이더가 아닙니다.")

            Then("배달을 수락할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.acceptDelivery(
                        request = request,
                        getOrder = { order },
                        getRider = { invalidUser }
                    )
                }
                ex.message shouldBe "라이더가 아닙니다."
            }
        }
    }

})
