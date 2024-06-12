package hyuuny.fooddelivery.application.cart

import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GertCartItemOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<CartItemOptionRepository>()
    val useCase = CartItemOptionUseCase(repository)

    Given("유저의 카트에 담겨 있는") {
        val now = LocalDateTime.now()
        val cartItemOptions = listOf(
            CartItemOption(id = 1, cartItemId = 1, optionId = 1, createdAt = now),
            CartItemOption(id = 2, cartItemId = 1, optionId = 5, createdAt = now),
            CartItemOption(id = 3, cartItemId = 2, optionId = 9, createdAt = now),
        )
        coEvery { repository.findAllByCartItemIdIn(any()) } returns cartItemOptions

        `when`("품목에 추가한 옵션들을") {
            val result = useCase.getAllByCartItemIds(listOf(1, 2))

            then("조회할 수 있다.") {
                result.forEachIndexed { index, cartItemOption ->
                    cartItemOption.id shouldBe cartItemOptions[index].id
                    cartItemOption.cartItemId shouldBe cartItemOptions[index].cartItemId
                    cartItemOption.optionId shouldBe cartItemOptions[index].optionId
                }
            }
        }
    }
})