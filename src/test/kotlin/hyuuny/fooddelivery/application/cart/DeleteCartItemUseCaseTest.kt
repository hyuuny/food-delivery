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

class DeleteCartItemUseCaseTest : BehaviorSpec({

    val repository = mockk<CartRepository>()
    val itemRepository = mockk<CartItemRepository>()
    val itemOptionRepository = mockk<CartItemOptionRepository>()
    val useCase = CartUseCase(repository, itemRepository, itemOptionRepository)

    Given("장바구니 품목을 삭제하면서 변경하면서") {
        val cartId = 1L
        val cartItemId = 1L

        val now = LocalDateTime.now()
        val cartItem = CartItem(id = 1, cartId = cartId, menuId = 1, quantity = 1, createdAt = now, updatedAt = now)

        coEvery { repository.existsById(any()) } returns true
        coEvery { itemRepository.findByIdAndCartId(any(), any()) } returns cartItem
        coEvery { itemOptionRepository.deleteAllByCartItemId(any()) } returns Unit
        coEvery { itemRepository.delete(any()) } returns Unit

        `when`("존재하는 품목을 삭제하면") {
            useCase.deleteCartItem(cartId, cartItemId)

            then("정상적으로 삭제된다.") {
                coEvery { itemRepository.delete(any()) }
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

        `when`("존재하지 않는 장바구니 아이디이면") {
            coEvery { repository.existsById(any()) } returns false

            then("장바구니를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteCartItem(0, cartItemId)
                }
                ex.message shouldBe "0번 장바구니를 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 장바구니 품목 아이디이면") {
            coEvery { repository.existsById(any()) } returns true
            coEvery { itemRepository.findByIdAndCartId(any(), any()) } returns null

            then("장바구니 품목을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteCartItem(cartId, 0)
                }
                ex.message shouldBe "1번 장바구니의 0번 품목을 찾을 수 없습니다."
            }
        }
    }
})