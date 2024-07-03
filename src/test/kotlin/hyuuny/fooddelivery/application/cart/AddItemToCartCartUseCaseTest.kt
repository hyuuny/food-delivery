package hyuuny.fooddelivery.application.cart

import AddItemAndOptionRequest
import AddItemToCartRequest
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import hyuuny.fooddelivery.infrastructure.cart.CartRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class AddItemToCartCartUseCaseTest : BehaviorSpec({

    val repository = mockk<CartRepository>()
    val itemRepository = mockk<CartItemRepository>()
    val itemOptionRepository = mockk<CartItemOptionRepository>()
    val storeUseCase = mockk<StoreUseCase>()
    val useCase = CartUseCase(repository, itemRepository, itemOptionRepository)

    Given("장바구니에 메뉴를 등록하면") {
        val userId = 1L
        val storeId = 1L

        val now = LocalDateTime.now()
        val store = Store(
            id = storeId,
            categoryId = 2L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "백종원의 빽보이피자",
            ownerName = "나피자",
            taxId = "125-21-38923",
            deliveryFee = 0,
            minimumOrderAmount = 14000,
            iconImageUrl = "icon-image-url-1.jpg",
            description = "안녕하세요. 백종원이 빽보이피자입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립니다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "02-1231-2308",
            createdAt = now.minusYears(1),
            updatedAt = now.minusYears(1),
        )
        val request = AddItemToCartRequest(
            storeId = storeId,
            item = AddItemAndOptionRequest(
                menuId = 5,
                quantity = 2,
                optionIds = listOf(12, 15, 21)
            )
        )
        val cart = Cart(id = 1L, userId = 1, createdAt = now, updatedAt = now)
        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { repository.findByUserId(any()) } returns null
        coEvery { repository.insert(any()) } returns cart

        val cartItem = CartItem(id = 1, cartId = cart.id!!, menuId = 1, quantity = 1, createdAt = now, updatedAt = now)
        coEvery { itemRepository.insert(any()) } returns cartItem

        val cartItemOptions = request.item.optionIds!!.map {
            CartItemOption(id = 1, cartItemId = cartItem.id!!, optionId = it, createdAt = now)
        }
        coEvery { itemOptionRepository.insertAll(any()) } returns cartItemOptions

        `when`("선택한 품목과 옵션에 맞게") {
            val result = useCase.addItemToCart(
                userId = userId,
                getStore = { store },
                request = request
            )

            then("장바구니에 등록된다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe cart.userId
                result.storeId shouldBe cart.storeId
                result.deliveryFee shouldBe cart.deliveryFee
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe cart.createdAt
            }
        }

        `when`("존재하는 장바구니에 다른 매장의 메뉴를 담으면") {
            val cart = Cart(id = 1L, userId = 1, storeId = 40, createdAt = now, updatedAt = now)
            coEvery { repository.findByUserId(any()) } returns cart

            then("장바구니에는 같은 매장의 메뉴만 담을 수 있다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.addItemToCart(
                        userId = userId,
                        getStore = { store },
                        request = request.copy(storeId = 2)
                    )
                }
                ex.message shouldBe "장바구니에는 같은 매장의 메뉴만 담을 수 있습니다."
            }
        }

        `when`("옵션이 하나라도 없으면") {
            val badRequest = AddItemToCartRequest(
                storeId = storeId,
                item = AddItemAndOptionRequest(menuId = 5, quantity = 2, optionIds = emptyList())
            )
            then("품목 옵션은 필수값이라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.addItemToCart(userId = userId, getStore = { store }, request = badRequest)
                }
                ex.message shouldBe "품목 옵션은 필수값입니다."
            }
        }
    }
})