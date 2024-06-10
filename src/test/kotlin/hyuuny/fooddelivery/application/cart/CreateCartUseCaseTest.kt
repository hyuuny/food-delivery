package hyuuny.fooddelivery.application.cart

import AddItemAndOptionRequest
import AddItemToCartRequest
import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import hyuuny.fooddelivery.infrastructure.cart.CartRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateCartUseCaseTest : BehaviorSpec({

    val repository = mockk<CartRepository>()
    val itemRepository = mockk<CartItemRepository>()
    val itemOptionRepository = mockk<CartItemOptionRepository>()
    val useCase = CartUseCase(repository, itemRepository, itemOptionRepository)

    Given("장바구니에 메뉴를 등록하면") {
        val userId = 1L
        val request = AddItemToCartRequest(
            item = AddItemAndOptionRequest(
                menuId = 5,
                quantity = 2,
                optionIds = listOf(12, 15, 21)
            )
        )

        val now = LocalDateTime.now()
        val cart = Cart(id = 1L, userId = 1, createdAt = now, updatedAt = now)
        coEvery { repository.findByUserId(any()) } returns null
        coEvery { repository.insert(any()) } returns cart

        val cartItem = CartItem(id = 1, cartId = cart.id!!, menuId = 1, quantity = 1, createdAt = now, updatedAt = now)
        coEvery { itemRepository.insert(any()) } returns cartItem

        val cartItemOptions = request.item.optionIds!!.map {
            CartItemOption(id = 1, cartItemId = cartItem.id!!, optionId = it, createdAt = now)
        }
        coEvery { itemOptionRepository.insertAll(any()) } returns cartItemOptions

        `when`("선택한 메뉴와 옵션에 맞게") {
            val result = useCase.addItemToCart(userId, request)

            then("장바구니에 등록된다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe result.userId
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }
    }
})