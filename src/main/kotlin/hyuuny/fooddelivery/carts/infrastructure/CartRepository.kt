package hyuuny.fooddelivery.carts.infrastructure

import hyuuny.fooddelivery.carts.domain.Cart


interface CartRepository {

    suspend fun insert(cart: Cart): Cart

    suspend fun findById(id: Long): Cart?

    suspend fun findByUserId(userId: Long): Cart?

    suspend fun update(cart: Cart)

    suspend fun updateStoreIdAndDeliveryFee(cart: Cart)

    suspend fun existsById(id: Long): Boolean

}
