package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.Cart
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class CartRepositoryImpl(
    private val dao: CartDao,
    private val template: R2dbcEntityTemplate,
) : CartRepository {

    override suspend fun insert(cart: Cart): Cart = dao.save(cart)

    override suspend fun findById(id: Long): Cart? = dao.findById(id)

    override suspend fun findByUserId(userId: Long): Cart? = dao.findByUserId(userId)

    override suspend fun update(cart: Cart) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

}