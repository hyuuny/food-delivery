package hyuuny.fooddelivery.carts.application

import hyuuny.fooddelivery.carts.domain.CartItemOption
import hyuuny.fooddelivery.carts.infrastructure.CartItemOptionRepository
import org.springframework.stereotype.Component

@Component
class CartItemOptionUseCase(
    private val repository: CartItemOptionRepository,
) {

    suspend fun getAllByCartItemId(cartItemId: Long): List<CartItemOption> = repository.findAllByCartItemId(cartItemId)

    suspend fun getAllByCartItemIds(cartItemIds: List<Long>): List<CartItemOption> =
        repository.findAllByCartItemIdIn(cartItemIds)

}
