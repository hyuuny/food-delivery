package hyuuny.fooddelivery.infrastructure.order

import hyuuny.fooddelivery.domain.order.OrderItem

interface OrderItemRepository {

    suspend fun insert(orderItem: OrderItem): OrderItem

    suspend fun insertAll(orderItems: List<OrderItem>): List<OrderItem>

    suspend fun findById(id: Long): OrderItem?

    suspend fun findAllByOrderId(orderId: Long): List<OrderItem>

    suspend fun findAllByOrderIds(orderIds: List<Long>): List<OrderItem>

}
