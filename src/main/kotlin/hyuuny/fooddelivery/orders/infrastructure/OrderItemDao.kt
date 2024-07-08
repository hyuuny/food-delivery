package hyuuny.fooddelivery.orders.infrastructure

import hyuuny.fooddelivery.orders.domain.OrderItem
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderItemDao : CoroutineCrudRepository<OrderItem, Long> {

    suspend fun findAllByOrderId(orderId: Long): List<OrderItem>

    suspend fun findAllByOrderIdIn(orderIds: List<Long>): List<OrderItem>

    suspend fun findAllByMenuNameContains(menuName: String): List<OrderItem>

}
