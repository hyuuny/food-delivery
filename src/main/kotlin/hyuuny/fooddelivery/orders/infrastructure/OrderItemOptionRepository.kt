package hyuuny.fooddelivery.orders.infrastructure

import hyuuny.fooddelivery.orders.domain.OrderItemOption

interface OrderItemOptionRepository {

    suspend fun insertAll(orderItemOptions: List<OrderItemOption>): List<OrderItemOption>

    suspend fun findAllByOrderItemId(orderItemId: Long): List<OrderItemOption>

    suspend fun findAllByOrderItemIdIn(orderItemIds: List<Long>): List<OrderItemOption>

}
