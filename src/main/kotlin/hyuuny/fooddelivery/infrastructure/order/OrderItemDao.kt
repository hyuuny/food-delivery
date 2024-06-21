package hyuuny.fooddelivery.infrastructure.order

import hyuuny.fooddelivery.domain.order.OrderItem
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderItemDao : CoroutineCrudRepository<OrderItem, Long> {

    suspend fun findAllByOrderId(orderId: Long): List<OrderItem>

    suspend fun findAllByOrderIdIn(orderIds: List<Long>): List<OrderItem>

    suspend fun findAllByMenuNameContains(menuName: String): List<OrderItem>

}
