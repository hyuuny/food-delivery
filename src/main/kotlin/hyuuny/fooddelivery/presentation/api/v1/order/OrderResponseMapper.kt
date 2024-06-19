package hyuuny.fooddelivery.presentation.api.v1.order

import hyuuny.fooddelivery.application.order.OrderItemOptionUseCase
import hyuuny.fooddelivery.application.order.OrderItemUseCase
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class OrderResponseMapper(
    private val orderItemUseCase: OrderItemUseCase,
    private val orderItemOptionUseCase: OrderItemOptionUseCase,
) {

    suspend fun mapToOrderResponse(order: Order): OrderResponse = coroutineScope {
        val orderItems = async { orderItemUseCase.getAllByOrderId(order.id!!) }.await()
        val orderItemOptionGroup = async {
            orderItemOptionUseCase.getAllByOrderItemIdIn(orderItems.mapNotNull { it.id }).groupBy { it.orderItemId }
        }.await()

        val orderItemResponses = orderItems.mapNotNull { orderItem ->
            val optionsOfOrderItem = orderItemOptionGroup[orderItem.id] ?: return@mapNotNull null
            val orderItemOptionResponses = optionsOfOrderItem.map { orderItemOption ->
                OrderItemOptionResponse.from(orderItemOption)
            }
            OrderItemResponse.from(orderItem, orderItemOptionResponses)
        }
        OrderResponse.from(order, orderItemResponses)
    }

}
