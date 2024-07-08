package hyuuny.fooddelivery.carts.infrastructure

import hyuuny.fooddelivery.carts.domain.Cart
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
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
        template.update<Cart>()
            .matching(
                Query.query(
                    where("id").`is`(cart.id!!)
                ),
            ).applyAndAwait(
                Update.update("updated_at", cart.updatedAt)
            )
    }

    override suspend fun updateStoreIdAndDeliveryFee(cart: Cart) {
        template.update<Cart>()
            .matching(
                Query.query(
                    where("id").`is`(cart.id!!)
                ),
            ).applyAndAwait(
                Update.update("store_id", cart.storeId)
                    .set("delivery_fee", cart.deliveryFee)
                    .set("updated_at", cart.updatedAt)
            )
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

}
