package hyuuny.fooddelivery.infrastructure.order

import ApiOrderSearchCondition
import hyuuny.fooddelivery.domain.order.Order
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl(
    private val dao: OrderDao,
    private val template: R2dbcEntityTemplate,
) : OrderRepository {

    override suspend fun insert(order: Order): Order = dao.save(order)

    override suspend fun findById(id: Long): Order? {
        TODO("Not yet implemented")
    }

    override suspend fun updateStatus(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun findAllOrders(searchCondition: ApiOrderSearchCondition, pageable: Pageable): PageImpl<Order> {
        TODO("Not yet implemented")
    }
}
