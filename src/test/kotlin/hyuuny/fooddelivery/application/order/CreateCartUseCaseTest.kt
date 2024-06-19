package hyuuny.fooddelivery.application.order

import CreateOrderItemRequest
import CreateOrderRequest
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.domain.order.OrderItem
import hyuuny.fooddelivery.domain.order.OrderItemOption
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.order.OrderItemOptionRepository
import hyuuny.fooddelivery.infrastructure.order.OrderItemRepository
import hyuuny.fooddelivery.infrastructure.order.OrderRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreateCartUseCaseTest : BehaviorSpec({

    val orderRepository = mockk<OrderRepository>()
    val orderItemRepository = mockk<OrderItemRepository>()
    val orderItemOptionRepository = mockk<OrderItemOptionRepository>()
    val orderCartValidator = mockk<OrderCartValidator>()
    val userUseCase = mockk<UserUseCase>()
    val menuUseCase = mockk<MenuUseCase>()
    val optionUseCase = mockk<OptionUseCase>()

    val useCase = OrderUseCase(
        orderRepository,
        orderItemRepository,
        orderItemOptionRepository,
        orderCartValidator,
    )

    Given("회원이 음식을 주문하면서") {
        val cartId = 1L
        val userId = 1L
        val storeId = 1L

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

        val request = CreateOrderRequest(
            storeId = 1,
            paymentMethod = PaymentMethod.NAVER_PAY,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 23000,
            deliveryFee = 0,
            orderItems = listOf(
                CreateOrderItemRequest(menuId = 1, quantity = 1, optionIds = listOf(1, 2)),
                CreateOrderItemRequest(menuId = 2, quantity = 1, optionIds = listOf(3))
            )
        )

        val orderNumber = "O_${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}"
        val paymentId = "PAY_${UUID.randomUUID().toString().replace("-", "")}"
        val order = Order(
            id = 1L,
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

        val menus = listOf(
            Menu(id = 1, menuGroupId = 1L, name = "피자", price = 15000, createdAt = now, updatedAt = now),
            Menu(id = 2, menuGroupId = 3L, name = "불고기 버거", price = 5000, createdAt = now, updatedAt = now),
        )
        val options = listOf(
            Option(id = 1, optionGroupId = 1L, name = "페퍼로니", price = 1000, createdAt = now, updatedAt = now),
            Option(id = 2, optionGroupId = 1L, name = "제로콜라", price = 1500, createdAt = now, updatedAt = now),
            Option(id = 3, optionGroupId = 2L, name = "콘 아이스크림", price = 500, createdAt = now, updatedAt = now),
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

        coEvery { orderCartValidator.validate(any(), any()) } returns Unit
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { menuUseCase.getAllByIds(any()) } returns menus
        coEvery { optionUseCase.getAllByIds(any()) } returns options

        coEvery { orderItemRepository.insert(any()) } returns orderItems[0]
        coEvery { orderItemRepository.insert(any()) } returns orderItems[1]
        coEvery { orderItemOptionRepository.insertAll(any()) } returns orderItemOptions
        coEvery { orderRepository.insert(any()) } returns order

        `when`("유효한 주문 요청이 주어지면") {
            val result = useCase.createOrder(
                cartId = cartId,
                request = request,
                getUser = { userUseCase.getUser(userId) },
                getMenus = { menuUseCase.getAllByIds(request.orderItems.map { it.menuId }) },
                getOptions = { optionUseCase.getAllByIds(request.orderItems.flatMap { it.optionIds }) }
            )

            then("주문이 정상적으로 생성된다") {
                result.id.shouldNotBeNull()
                result.orderNumber shouldBe orderNumber
                result.userId shouldBe userId
                result.storeId shouldBe request.storeId
                result.paymentId shouldBe paymentId
                result.paymentMethod shouldBe request.paymentMethod
                result.status shouldBe OrderStatus.CREATED
                result.deliveryType shouldBe request.deliveryType
                result.zipCode shouldBe request.zipCode
                result.address shouldBe request.address
                result.detailAddress shouldBe request.detailAddress
                result.phoneNumber shouldBe request.phoneNumber
                result.messageToRider shouldBe request.messageToRider
                result.messageToStore shouldBe request.messageToStore
                result.totalPrice shouldBe request.totalPrice
                result.deliveryFee shouldBe request.deliveryFee
            }
        }

        `when`("장바구니 결제 금액과 일치하지 않으면") {
            val invalidRequest = request.copy(totalPrice = -1)
            coEvery {
                orderCartValidator.validate(
                    any(),
                    any()
                )
            } throws IllegalStateException("장바구니 금액과 주문 금액이 일치하지 않습니다.")

            then("주문 생성이 실패한다") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.createOrder(
                        cartId = cartId,
                        request = invalidRequest,
                        getUser = { userUseCase.getUser(userId) },
                        getMenus = { menuUseCase.getAllByIds(invalidRequest.orderItems.map { it.menuId }) },
                        getOptions = { optionUseCase.getAllByIds(invalidRequest.orderItems.flatMap { it.optionIds }) }
                    )
                }
                ex.message shouldBe "장바구니 금액과 주문 금액이 일치하지 않습니다."
            }
        }

        `when`("장바구니를 찾을 수 없다면") {
            coEvery { orderCartValidator.validate(any(), any()) } throws NoSuchElementException("0번 장바구니를 찾을 수 없습니다.")

            then("주문 생성이 실패한다") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createOrder(
                        cartId = 0,
                        request = request,
                        getUser = { userUseCase.getUser(userId) },
                        getMenus = { menuUseCase.getAllByIds(request.orderItems.map { it.menuId }) },
                        getOptions = { optionUseCase.getAllByIds(request.orderItems.flatMap { it.optionIds }) }
                    )
                }
                ex.message shouldBe "0번 장바구니를 찾을 수 없습니다."
            }
        }

        `when`("장바구니에 담긴 메뉴가 일치하지 않으면") {
            val cartOrderItems = listOf(CreateOrderItemRequest(menuId = 1, quantity = 1, optionIds = listOf(1, 2)))
            val invalidRequest = request.copy(orderItems = cartOrderItems)
            coEvery {
                orderCartValidator.validate(
                    any(),
                    any()
                )
            } throws IllegalStateException("장바구니에 담긴 메뉴와 일치하지 않습니다.")

            then("주문 생성이 실패한다") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.createOrder(
                        cartId = cartId,
                        request = invalidRequest,
                        getUser = { userUseCase.getUser(userId) },
                        getMenus = { menuUseCase.getAllByIds(invalidRequest.orderItems.map { it.menuId }) },
                        getOptions = { optionUseCase.getAllByIds(invalidRequest.orderItems.flatMap { it.optionIds }) }
                    )
                }
                ex.message shouldBe "장바구니에 담긴 메뉴와 일치하지 않습니다."
            }
        }

        `when`("장바구니에 담긴 메뉴의 옵션과 일치하지 않으면") {
            val cartOrderItems = listOf(
                CreateOrderItemRequest(menuId = 1, quantity = 1, optionIds = listOf(1, 2)),
                CreateOrderItemRequest(menuId = 2, quantity = 1, optionIds = listOf(50))
            )
            val invalidRequest = request.copy(orderItems = cartOrderItems)
            coEvery {
                orderCartValidator.validate(
                    any(),
                    any()
                )
            } throws IllegalStateException("장바구니에 담긴 메뉴와 일치하지 않습니다.")

            then("주문 생성이 실패한다") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.createOrder(
                        cartId = cartId,
                        request = invalidRequest,
                        getUser = { userUseCase.getUser(userId) },
                        getMenus = { menuUseCase.getAllByIds(invalidRequest.orderItems.map { it.menuId }) },
                        getOptions = { optionUseCase.getAllByIds(invalidRequest.orderItems.flatMap { it.optionIds }) }
                    )
                }
                ex.message shouldBe "장바구니에 담긴 메뉴와 일치하지 않습니다."
            }
        }
    }

})
