package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.CartItem
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component

@Component
class CartItemRepositoryImpl(
    private val dao: CartItemDao,
    private val template: R2dbcEntityTemplate,
) : CartItemRepository {

    override suspend fun insert(cartItem: CartItem): CartItem = dao.save(cartItem)

    override suspend fun findById(id: Long): CartItem? = dao.findById(id)

    override suspend fun findAllByCartId(cartId: Long): List<CartItem> = dao.findAllByCartId(cartId)

    override suspend fun update(cartItem: CartItem) {
        template.update<CartItem>()
            .matching(
                Query.query(
                    Criteria.where("id").`is`(cartItem.id!!)
                ),
            ).applyAndAwait(
                Update.update("quantity", cartItem.quantity)
                    .set("updated_at", cartItem.updatedAt)
            )
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

}