package hyuuny.fooddelivery.orders.application

import hyuuny.fooddelivery.orders.domain.OrderItem
import hyuuny.fooddelivery.orders.infrastructure.OrderItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderItemUseCase(
    private val repository: OrderItemRepository
) {

    suspend fun getAllByOrderId(orderId: Long): List<OrderItem> = repository.findAllByOrderId(orderId)

    suspend fun getAllByOrderIdIn(orderIds: List<Long>): List<OrderItem> = repository.findAllByOrderIds(orderIds)

}
