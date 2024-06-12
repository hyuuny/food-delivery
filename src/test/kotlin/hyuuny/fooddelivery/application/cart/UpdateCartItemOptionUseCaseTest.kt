package hyuuny.fooddelivery.application.cart

import UpdateCartItemOptionsRequest
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import hyuuny.fooddelivery.infrastructure.cart.CartRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class UpdateCartItemOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<CartRepository>()
    val itemRepository = mockk<CartItemRepository>()
    val itemOptionRepository = mockk<CartItemOptionRepository>()
    val useCase = CartUseCase(repository, itemRepository, itemOptionRepository)

    Given("장바구니 품목의 옵션을 변경하면서") {
        val cartId = 1L
        val cartItemId = 3L
        val request = UpdateCartItemOptionsRequest(optionIds = listOf(7, 13, 89, 20))

        val now = LocalDateTime.now()
        coEvery { repository.existsById(any()) } returns true
        val cartItem = CartItem(id = 1, cartId = cartId, menuId = 1, quantity = 1, createdAt = now, updatedAt = now)
        coEvery { itemRepository.findById(any()) } returns cartItem
        coEvery { itemOptionRepository.deleteAllByCartItemId(any()) } returns Unit
        coEvery { itemOptionRepository.insertAll(any()) } returns listOf(
            CartItemOption(id = 6, cartItemId = cartItemId, optionId = 7, createdAt = now),
            CartItemOption(id = 7, cartItemId = cartItemId, optionId = 13, createdAt = now),
            CartItemOption(id = 8, cartItemId = cartItemId, optionId = 89, createdAt = now),
            CartItemOption(id = 9, cartItemId = cartItemId, optionId = 20, createdAt = now),
        )

        `when`("올바른 옵션을 입력하면서") {
            useCase.updateCartItemOptions(cartId, cartItemId, request)

            then("품목의 옵션들이 변경된다.") {
                coEvery { itemOptionRepository.insertAll(any()) }
            }
        }

        `when`("선택한 옵션이 없으면") {
            val badRequest = UpdateCartItemOptionsRequest(optionIds = emptyList())

            then("품목 옵션은 필수값이라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.updateCartItemOptions(cartId, cartItemId, badRequest)
                }
                ex.message shouldBe "품목 옵션은 필수값입니다."
            }
        }

        `when`("존재하지 않는 장바구니 아이디이면") {
            coEvery { repository.existsById(any()) } returns false

            then("장바구니를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.updateCartItemOptions(0, cartItemId, request)
                }
                ex.message shouldBe "0번 장바구니를 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 장바구니 품목 아이디이면") {
            coEvery { repository.existsById(any()) } returns true
            coEvery { itemRepository.findById(any()) } returns null

            then("장바구니 품목을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.updateCartItemOptions(cartId, 0, request)
                }
                ex.message shouldBe "0번 장바구니 품목을 찾을 수 없습니다."
            }
        }
    }
})