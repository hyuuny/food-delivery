package hyuuny.fooddelivery.carts.infrastructure

import hyuuny.fooddelivery.carts.domain.CartItem
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CartItemDao : CoroutineCrudRepository<CartItem, Long> {

    suspend fun findAllByCartId(cartId: Long): List<CartItem>

    suspend fun findByIdAndCartId(id: Long, cartId: Long): CartItem?

    suspend fun deleteAllByCartId(cartId: Long)

}
