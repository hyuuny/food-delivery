package hyuuny.fooddelivery.infrastructure.cart

import hyuuny.fooddelivery.domain.cart.CartItemOption
import kotlinx.coroutines.flow.toList
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class CartItemOptionRepositoryImpl(
    private val dao: CartItemOptionDao,
    private val template: R2dbcEntityTemplate,
) : CartItemOptionRepository {

    override suspend fun insertAll(cartItemOption: List<CartItemOption>): List<CartItemOption> =
        dao.saveAll(cartItemOption).toList()

    override suspend fun findAllByCartItemId(cartItemId: Long): List<CartItemOption> =
        dao.findAllByCartItemId(cartItemId)

    override suspend fun findAllByCartItemIdIn(cartItemIds: List<Long>): List<CartItemOption> =
        dao.findAllByCartItemIdIn(cartItemIds)

    override suspend fun deleteAllByCartItemId(cartItemId: Long) {
        TODO("Not yet implemented")
    }

}