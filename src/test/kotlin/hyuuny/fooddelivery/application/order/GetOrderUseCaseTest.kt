package hyuuny.fooddelivery.application.order

import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.order.OrderItemOptionRepository
import hyuuny.fooddelivery.infrastructure.order.OrderItemRepository
import hyuuny.fooddelivery.infrastructure.order.OrderRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GetOrderUseCaseTest : BehaviorSpec({

    val orderRepository = mockk<OrderRepository>()
    val orderItemRepository = mockk<OrderItemRepository>()
    val orderItemOptionRepository = mockk<OrderItemOptionRepository>()
    val orderCartValidator = mockk<OrderCartValidator>()
    val userUseCase = mockk<UserUseCase>()

    val useCase = OrderUseCase(
        orderRepository,
        orderItemRepository,
        orderItemOptionRepository,
        orderCartValidator,
    )

    Given("주문내역을 상세조회 할 때") {
        val userId = 1L
        val storeId = 1L
        val orderId = 130L

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

        val orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}"
        val paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "")}"
        val order = Order(
            id = orderId,
            orderNumber = orderNumber,
            userId = userId,
            storeId = storeId,
            paymentId = paymentId,
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 23000,
            deliveryFee = 0,
            createdAt = now,
            updatedAt = now,
        )

        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { orderRepository.findByIdAndUserId(any(), any()) } returns order

        `when`("존재하는 내역이면") {
            val result = useCase.getOrder(orderId) { userUseCase.getUser(userId) }

            then("정상적으로 조회할 수 있다.") {
                result.id shouldBe order.id!!
                result.orderNumber shouldBe orderNumber
                result.userId shouldBe order.userId
                result.storeId shouldBe order.storeId
                result.paymentId shouldBe order.paymentId
                result.paymentMethod shouldBe order.paymentMethod
                result.status shouldBe order.status
                result.deliveryType shouldBe order.deliveryType
                result.zipCode shouldBe order.zipCode
                result.address shouldBe order.address
                result.detailAddress shouldBe order.detailAddress
                result.phoneNumber shouldBe order.phoneNumber
                result.messageToRider shouldBe order.messageToRider
                result.messageToStore shouldBe order.messageToStore
                result.totalPrice shouldBe order.totalPrice
                result.deliveryFee shouldBe order.deliveryFee
            }
        }

        `when`("존재하지 않는 내역이면") {
            coEvery { orderRepository.findByIdAndUserId(any(), any()) } returns null

            then("주문을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getOrder(0) { userUseCase.getUser(userId) }
                }
                ex.message shouldBe "0번 주문을 찾을 수 없습니다."
            }
        }

        `when`("회원이 아니라면") {
            coEvery { userUseCase.getUser(any()) } throws NoSuchElementException("0번 회원을 찾을 수 없습니다.")

            then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getOrder(orderId) { userUseCase.getUser(0) }
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }
    }

})
