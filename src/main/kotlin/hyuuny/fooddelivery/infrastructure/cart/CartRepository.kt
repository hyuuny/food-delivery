package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.Cart

interface CartRepository {

    suspend fun insert(cart: Cart): Cart

    suspend fun findById(id: Long): Cart?

    suspend fun findByUserId(userId: Long): Cart?

    suspend fun update(cart: Cart)

    suspend fun delete(id: Long)

    suspend fun existsById(id: Long): Boolean

}