package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.CartItem
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CartItemDao : CoroutineCrudRepository<CartItem, Long> {

    suspend fun findAllByCartId(cartId: Long): List<CartItem>

    suspend fun findByIdAndCartId(id: Long, cartId: Long): CartItem?

}