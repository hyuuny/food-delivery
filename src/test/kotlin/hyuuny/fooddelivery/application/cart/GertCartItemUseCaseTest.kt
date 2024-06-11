package hyuuny.fooddelivery.application.cart

import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GertCartItemUseCaseTest : BehaviorSpec({

    val repository = mockk<CartItemRepository>()
    val useCase = CartItemUseCase(repository)

    Given("유저의 카트에 담겨 있는") {
        val cartId = 2L
        val now = LocalDateTime.now()
        val cartItems = listOf(
            CartItem(id = 1, cartId = cartId, menuId = 1, quantity = 1, createdAt = now, updatedAt = now),
            CartItem(id = 2, cartId = cartId, menuId = 1, quantity = 2, createdAt = now, updatedAt = now),
            CartItem(id = 3, cartId = cartId, menuId = 2, quantity = 1, createdAt = now, updatedAt = now),
        )
        coEvery { repository.findAllByCartId(any()) } returns cartItems

        `when`("아이템들을") {
            val result = useCase.getAllByCartId(cartId)

            then("조회할 수 있다.") {
                result.forEachIndexed { index, item ->
                    item.id shouldBe cartItems[index].id
                    item.cartId shouldBe cartItems[index].cartId
                    item.menuId shouldBe cartItems[index].menuId
                    item.quantity shouldBe cartItems[index].quantity
                }
            }
        }
    }
})