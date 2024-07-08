package hyuuny.fooddelivery.carts.infrastructure

import hyuuny.fooddelivery.carts.domain.CartItem

interface CartItemRepository {

    suspend fun insert(cartItem: CartItem): CartItem

    suspend fun findById(id: Long): CartItem?

    suspend fun findAllByCartId(cartId: Long): List<CartItem>

    suspend fun update(cartItem: CartItem)

    suspend fun updateUpdatedAt(cartItem: CartItem)

    suspend fun delete(id: Long)

    suspend fun findByIdAndCartId(id: Long, cartId: Long): CartItem?

    suspend fun deleteAllByCartId(cartId: Long)

}
