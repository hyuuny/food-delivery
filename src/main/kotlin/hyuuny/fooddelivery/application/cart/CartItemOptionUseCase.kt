package hyuuny.fooddelivery.application.cart

import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import org.springframework.stereotype.Component

@Component
class CartItemOptionUseCase(
    private val repository: CartItemOptionRepository,
) {

    suspend fun getAllByCartItemId(cartItemId: Long): List<CartItemOption> = repository.findAllByCartItemId(cartItemId)

    suspend fun getAllByCartItemIds(cartItemIds: List<Long>): List<CartItemOption> =
        repository.findAllByCartItemIdIn(cartItemIds)

}