package hyuuny.fooddelivery.carts.application

import hyuuny.fooddelivery.carts.domain.CartItem
import hyuuny.fooddelivery.carts.infrastructure.CartItemRepository
import org.springframework.stereotype.Component

@Component
class CartItemUseCase(
    private val repository: CartItemRepository,
) {

    suspend fun getAllByCartId(cartId: Long): List<CartItem> = repository.findAllByCartId(cartId)

}
