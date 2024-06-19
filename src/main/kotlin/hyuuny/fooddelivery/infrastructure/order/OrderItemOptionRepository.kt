package hyuuny.fooddelivery.infrastructure.order

import hyuuny.fooddelivery.domain.order.OrderItemOption

interface OrderItemOptionRepository {

    suspend fun insertAll(orderItemOptions: List<OrderItemOption>): List<OrderItemOption>

    suspend fun findAllByOrderItemId(orderItemId: Long): List<OrderItemOption>

    suspend fun findAllByOrderItemIdIn(orderItemIds: List<Long>): List<OrderItemOption>

}
