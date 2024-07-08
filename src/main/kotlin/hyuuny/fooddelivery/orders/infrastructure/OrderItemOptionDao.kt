package hyuuny.fooddelivery.orders.infrastructure

import hyuuny.fooddelivery.orders.domain.OrderItemOption
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderItemOptionDao : CoroutineCrudRepository<OrderItemOption, Long> {

    suspend fun findByOrderItemId(orderItemId: Long): List<OrderItemOption>

    suspend fun findAllByOrderItemIdIn(orderItemIds: List<Long>): List<OrderItemOption>

}
