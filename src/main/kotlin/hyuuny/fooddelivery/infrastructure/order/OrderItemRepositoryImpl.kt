package hyuuny.fooddelivery.infrastructure.order

import hyuuny.fooddelivery.domain.order.OrderItem
import kotlinx.coroutines.flow.toList
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class OrderItemRepositoryImpl(
    private val dao: OrderItemDao,
    private val template: R2dbcEntityTemplate,
) : OrderItemRepository {

    override suspend fun insert(orderItem: OrderItem): OrderItem = dao.save(orderItem)

    override suspend fun insertAll(orderItems: List<OrderItem>): List<OrderItem> = dao.saveAll(orderItems).toList()

    override suspend fun findById(id: Long): OrderItem? = dao.findById(id)

    override suspend fun findAllByOrderId(orderId: Long): List<OrderItem> = dao.findAllByOrderId(orderId).toList()

}
