package hyuuny.fooddelivery.application.order

import hyuuny.fooddelivery.domain.order.OrderItemOption
import hyuuny.fooddelivery.infrastructure.order.OrderItemOptionRepository
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
