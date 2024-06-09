package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.CartItem
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class CartItemRepositoryImpl(
    private val dao: CartItemDao,
    private val template: R2dbcEntityTemplate,
) : CartItemRepository {

    override suspend fun insert(cartItem: CartItem): CartItem = dao.save(cartItem)

    override suspend fun findById(id: Long): CartItem? {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByCartId(cartId: Long): List<CartItem> = dao.findAllByCartId(cartId)

    override suspend fun update(cartItem: CartItem) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

}