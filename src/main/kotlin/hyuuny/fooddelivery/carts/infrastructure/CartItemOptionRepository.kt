package hyuuny.fooddelivery.carts.infrastructure

import hyuuny.fooddelivery.carts.domain.CartItemOption


interface CartItemOptionRepository {

    suspend fun insertAll(cartItemOption: List<CartItemOption>): List<CartItemOption>

    suspend fun findAllByCartItemId(cartItemId: Long): List<CartItemOption>

    suspend fun findAllByCartItemIdIn(cartItemIds: List<Long>): List<CartItemOption>

    suspend fun deleteAllByCartItemId(cartItemId: Long)

    suspend fun deleteAllByCartItemIdIn(cartItemIds: List<Long>)

}
