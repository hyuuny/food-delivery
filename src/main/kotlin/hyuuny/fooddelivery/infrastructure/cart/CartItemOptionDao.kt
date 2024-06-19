package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.CartItemOption
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CartItemOptionDao : CoroutineCrudRepository<CartItemOption, Long> {

    suspend fun findAllByCartItemId(id: Long): List<CartItemOption>

    suspend fun findAllByCartItemIdIn(cartItemIds: List<Long>): List<CartItemOption>

    suspend fun deleteAllByCartItemId(cartItemId: Long)

    suspend fun deleteAllByCartItemIdIn(cartItemIds: List<Long>)

}