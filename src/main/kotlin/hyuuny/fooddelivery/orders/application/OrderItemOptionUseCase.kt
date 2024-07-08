package hyuuny.fooddelivery.orders.application

import hyuuny.fooddelivery.orders.domain.OrderItemOption
import hyuuny.fooddelivery.orders.infrastructure.OrderItemOptionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderItemOptionUseCase(
    private val repository: OrderItemOptionRepository
) {

    suspend fun getAllByOrderItemIdIn(orderItemIds: List<Long>): List<OrderItemOption> =
        repository.findAllByOrderItemIdIn(orderItemIds)

}
