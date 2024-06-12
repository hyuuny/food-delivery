package hyuuny.fooddelivery.application.cart

import UpdateCartItemQuantityRequest
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import hyuuny.fooddelivery.infrastructure.cart.CartRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class UpdateCartItemQuantityUseCaseTest : BehaviorSpec({

    val repository = mockk<CartRepository>()
    val itemRepository = mockk<CartItemRepository>()
    val itemOptionRepository = mockk<CartItemOptionRepository>()
    val useCase = CartUseCase(repository, itemRepository, itemOptionRepository)

    Given("장바구니 품목의 수량을 변경하면서") {
        val cartId = 1L
        val cartItemId = 1L
        val now = LocalDateTime.now()
        val cartItem =
            CartItem(id = cartItemId, cartId = cartId, menuId = 1, quantity = 1, createdAt = now, updatedAt = now)
        val request = UpdateCartItemQuantityRequest(quantity = 2)
        coEvery { repository.existsById(any()) } returns true
        coEvery { itemRepository.findById(any()) } returns cartItem
        coEvery { itemRepository.update(any()) } returns Unit


        `when`("수량이 0보다 크면") {
            useCase.updateCartItemQuantity(cartId, cartItemId, request)

            then("정상적으로 변경된다.") {
                coEvery { itemRepository.update(any()) }
            }
        }

        `when`("수량이 0이하라면") {
            val invalidRequest = UpdateCartItemQuantityRequest(quantity = 0)

            then("수량은 0보다 커야한다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.updateCartItemQuantity(cartId, cartItemId, invalidRequest)
                }
                ex.message shouldBe "수량은 0보다 커야합니다."
            }
        }

        `when`("존재하지 않는 장바구니의 품목 수량을 변경하면") {
            coEvery { repository.existsById(any()) } returns false

            then("장바구니를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.updateCartItemQuantity(0, cartItemId, request)
                }
                ex.message shouldBe "0번 장바구니를 찾을 수 없습니다."
            }
        }
    }
})