package hyuuny.fooddelivery.infrastructure.order

import ApiOrderSearchCondition
import hyuuny.fooddelivery.domain.order.Order
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface OrderRepository {

    suspend fun insert(order: Order): Order

    suspend fun findById(id: Long): Order?

    suspend fun findByIdAndUserId(id: Long, userId: Long): Order?

    suspend fun updateStatus(order: Order)

    suspend fun findAllOrders(searchCondition: ApiOrderSearchCondition, pageable: Pageable): PageImpl<Order>

}
