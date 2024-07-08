package hyuuny.fooddelivery.carts.application

import hyuuny.fooddelivery.carts.domain.Cart
import hyuuny.fooddelivery.carts.infrastructure.CartItemOptionRepository
import hyuuny.fooddelivery.carts.infrastructure.CartItemRepository
import hyuuny.fooddelivery.carts.infrastructure.CartRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GertCartUseCaseTest : BehaviorSpec({

    val repository = mockk<CartRepository>()
    val itemRepository = mockk<CartItemRepository>()
    val itemOptionRepository = mockk<CartItemOptionRepository>()
    val useCase = CartUseCase(repository, itemRepository, itemOptionRepository)

    Given("장바구니를 상세 조회할 때") {
        val userId = 1L
        val now = LocalDateTime.now()
        val cart = Cart(id = 1L, userId = 1, createdAt = now, updatedAt = now)
        coEvery { repository.findByUserId(any()) } returns cart

        `when`("장바구니에") {
            val result = useCase.getOrInsertCart(userId)

            then("담겨있는 품목과 옵션을 볼 수 있다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe result.userId
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }
    }
})
