package hyuuny.fooddelivery.application.cart

import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import hyuuny.fooddelivery.infrastructure.cart.CartRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class ExistsCartByUserIdAndStoreIdUseCaseTest : BehaviorSpec({

    val repository = mockk<CartRepository>()
    val itemRepository = mockk<CartItemRepository>()
    val itemOptionRepository = mockk<CartItemOptionRepository>()
    val useCase = CartUseCase(repository, itemRepository, itemOptionRepository)

    Given("장바구니의 매장 아이디와") {
        val userId = 1L
        val storeId = 1L

        val now = LocalDateTime.now()
        val cart = Cart(id = 1L, userId = userId, storeId = storeId, createdAt = now, updatedAt = now)
        coEvery { repository.findByUserId(any()) } returns cart

        `when`("같으면") {
            val result = useCase.existsCartByUserIdAndStoreId(userId, storeId)

            then("true를 반환한다.") {
                result shouldBe true
            }
        }

        `when`("다르면") {
            coEvery { repository.findByUserId(any()) } returns null
            val result = useCase.existsCartByUserIdAndStoreId(userId, storeId)

            then("false를 반환한다.") {
                result shouldBe false
            }
        }
    }
})
