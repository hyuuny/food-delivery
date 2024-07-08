package hyuuny.fooddelivery.orders.infrastructure

import hyuuny.fooddelivery.orders.domain.OrderItemOption
import kotlinx.coroutines.flow.toList
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class OrderItemOptionRepositoryImpl(
    private val dao: OrderItemOptionDao,
    private val template: R2dbcEntityTemplate,
) : OrderItemOptionRepository {

    override suspend fun insertAll(orderItemOptions: List<OrderItemOption>): List<OrderItemOption> =
        dao.saveAll(orderItemOptions).toList()

    override suspend fun findAllByOrderItemId(orderItemId: Long): List<OrderItemOption> =
        dao.findByOrderItemId(orderItemId)

    override suspend fun findAllByOrderItemIdIn(orderItemIds: List<Long>): List<OrderItemOption> =
        dao.findAllByOrderItemIdIn(orderItemIds).toList()

}
