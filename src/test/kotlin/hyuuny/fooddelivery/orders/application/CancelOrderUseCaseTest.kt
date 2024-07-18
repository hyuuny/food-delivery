package hyuuny.fooddelivery.orders.application

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.orders.infrastructure.OrderItemOptionRepository
import hyuuny.fooddelivery.orders.infrastructure.OrderItemRepository
import hyuuny.fooddelivery.orders.infrastructure.OrderRepository
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CancelOrderUseCaseTest : BehaviorSpec({

    val repository = mockk<OrderRepository>()
    val orderItemRepository = mockk<OrderItemRepository>()
    val orderItemOptionRepository = mockk<OrderItemOptionRepository>()
    val orderCartVerifier = mockk<OrderCartVerifier>()
    val orderDiscountVerifier = mockk<OrderDiscountVerifier>()
    val userUseCase = mockk<UserUseCase>()

    val useCase = OrderUseCase(
        repository,
        orderItemRepository,
        orderItemOptionRepository,
        orderCartVerifier,
        orderDiscountVerifier,
    )

    Given("회원이 주문을 취소할 때") {
        val userId = 1L
        val storeId = 1L
        val orderId = 1L

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

        val order = Order(
            id = orderId,
            orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
            userId = userId,
            storeId = storeId,
            categoryId = 1L,
            couponId = null,
            paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "").substring(0, 10)}",
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            orderPrice = 23000,
            couponDiscountAmount = 0,
            totalPrice = 23000,
            deliveryFee = 0,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { repository.findByIdAndUserId(any(), any()) } returns order
        coEvery { repository.updateStatus(any()) } returns Unit

        `when`("취소 가능한 상태이면") {
            useCase.cancelOrder(id = orderId, getUser = { userUseCase.getUser(userId) })

            then("주문이 정상적으로 취소된다") {
                coVerify { repository.updateStatus(any()) }
            }
        }

        `when`("취소할 수 없는 상태이면") {
            val notCancelableOrder = Order(
                id = orderId,
                orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}",
                userId = userId,
                storeId = storeId,
                categoryId = 1L,
                couponId = null,
                paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "").substring(0, 10)}",
                paymentMethod = PaymentMethod.NAVER_PAY,
                status = OrderStatus.PROCESSING,
                deliveryType = DeliveryType.OUTSOURCING,
                zipCode = "12345",
                address = "서울시 강남구 역삼동",
                detailAddress = "위워크 빌딩 19층",
                phoneNumber = "010-1234-5678",
                messageToRider = "1층 로비에 보관 부탁드립니다",
                messageToStore = null,
                orderPrice = 23000,
                couponDiscountAmount = 0,
                totalPrice = 23000,
                deliveryFee = 0,
                createdAt = now,
                updatedAt = now,
            )
            then("주문 취소가 불가능하다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.cancelOrder(id = notCancelableOrder.id!!, getUser = { userUseCase.getUser(userId) })
                }
                ex.message shouldBe "주문 취소가 불가능합니다."
            }
        }
    }

})
