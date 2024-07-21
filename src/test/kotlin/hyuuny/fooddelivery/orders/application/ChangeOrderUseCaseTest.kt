package hyuuny.fooddelivery.orders.application

import ChangeOrderStatusRequest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.coupons.infrastructure.UserCouponRepository
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.orders.infrastructure.OrderItemOptionRepository
import hyuuny.fooddelivery.orders.infrastructure.OrderItemRepository
import hyuuny.fooddelivery.orders.infrastructure.OrderRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChangeOrderUseCaseTest : BehaviorSpec({

    val repository = mockk<OrderRepository>()
    val orderItemRepository = mockk<OrderItemRepository>()
    val orderItemOptionRepository = mockk<OrderItemOptionRepository>()
    val userCouponRepository = mockk<UserCouponRepository>()
    val orderCartVerifier = mockk<OrderCartVerifier>()
    val orderDiscountVerifier = mockk<OrderDiscountVerifier>()

    val useCase = OrderUseCase(
        repository,
        orderItemRepository,
        orderItemOptionRepository,
        userCouponRepository,
        orderCartVerifier,
        orderDiscountVerifier,
    )

    Given("주문의 상태를 변경할 때") {
        val userId = 1L
        val storeId = 1L
        val orderId = 1L

        val now = LocalDateTime.now()

        val request = ChangeOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY)
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
        coEvery { repository.findById(any()) } returns order
        coEvery { repository.updateStatus(any()) } returns Unit

        `when`("정상적인 요청이면") {
            useCase.changeOrderStatus(id = orderId, request = request)

            then("주문상태가 정상적으로 변경된다.") {
                coVerify { repository.updateStatus(any()) }
            }
        }

        `when`("존재하지 않는 주문이면") {
            coEvery { repository.findById(any()) } returns null

            then("주문을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeOrderStatus(id = 0, request = request)
                }
                ex.message shouldBe "0번 주문을 찾을 수 없습니다."
            }
        }
    }

})
