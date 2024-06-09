package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.Cart
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CartDao : CoroutineCrudRepository<Cart, Long> {

    suspend fun findByUserId(userId: Long): Cart?

}