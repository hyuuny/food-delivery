package hyuuny.fooddelivery.orders.infrastructure

import hyuuny.fooddelivery.orders.domain.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderDao : CoroutineCrudRepository<Order, Long> {

    suspend fun findByIdAndUserId(id: Long, userId: Long): Order?

    suspend fun findByOrderNumber(orderNumber: String): Order?

    suspend fun findAllByUserIdIn(userIds: List<Long>): List<Order>

    suspend fun findAllByStoreIdIn(storeIds: List<Long>): List<Order>

}
