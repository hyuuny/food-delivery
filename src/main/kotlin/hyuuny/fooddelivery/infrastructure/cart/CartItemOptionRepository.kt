package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.CartItemOption

interface CartItemOptionRepository {

    suspend fun insertAll(cartItemOption: List<CartItemOption>): List<CartItemOption>

    suspend fun findAllByCartItemId(cartItemId: Long): List<CartItemOption>

    suspend fun findAllByCartItemIdIn(cartItemIds: List<Long>): List<CartItemOption>

    suspend fun deleteAllByCartItemId(cartItemId: Long)

}