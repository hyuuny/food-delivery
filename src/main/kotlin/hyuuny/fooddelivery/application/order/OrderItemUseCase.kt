package hyuuny.fooddelivery.application.order

import hyuuny.fooddelivery.domain.order.OrderItem
import hyuuny.fooddelivery.infrastructure.order.OrderItemRepository
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
