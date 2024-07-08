package hyuuny.fooddelivery.carts.infrastructure

import hyuuny.fooddelivery.carts.domain.Cart
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CartDao : CoroutineCrudRepository<Cart, Long> {

    suspend fun findByUserId(userId: Long): Cart?

}
