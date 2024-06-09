package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.CartItem

interface CartItemRepository {

    suspend fun insert(cartItem: CartItem): CartItem

    suspend fun findById(id: Long): CartItem?

    suspend fun findAllByCartId(cartId: Long): List<CartItem>

    suspend fun update(cartItem: CartItem)

    suspend fun delete(id: Long)

}