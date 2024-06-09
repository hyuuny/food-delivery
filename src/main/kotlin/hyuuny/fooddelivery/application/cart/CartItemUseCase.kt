package hyuuny.fooddelivery.application.cart

import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import org.springframework.stereotype.Component

@Component
class CartItemUseCase(
    private val repository: CartItemRepository,
) {

    suspend fun getAllByCartId(cartId: Long): List<CartItem> = repository.findAllByCartId(cartId)

}