package hyuuny.fooddelivery.presentation.api.v1.order

import hyuuny.fooddelivery.application.order.OrderItemOptionUseCase
import hyuuny.fooddelivery.application.order.OrderItemUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderItemResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderResponse
import hyuuny.fooddelivery.presentation.api.v1.order.response.OrderResponses
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class OrderResponseMapper(
    private val orderItemUseCase: OrderItemUseCase,
    private val orderItemOptionUseCase: OrderItemOptionUseCase,
    private val storeUseCase: StoreUseCase,
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

    suspend fun mapToOrderResponses(orders: List<Order>): List<OrderResponses> {
        return coroutineScope {
            val orderIds = orders.mapNotNull { it.id }

            val orderItemsDeferred = async { orderItemUseCase.getAllByOrderIdIn(orderIds) }
            val storeIds = orders.map { it.storeId }
            val storesDeferred = async { storeUseCase.getAllByIds(storeIds) }

            val orderItems = orderItemsDeferred.await()
            val orderItemGroups = orderItems.groupBy { it.orderId }

            val stores = storesDeferred.await()
            val storeMap = stores.associateBy { it.id }

            orders.mapNotNull {
                val store = storeMap[it.storeId] ?: return@mapNotNull null
                val items = orderItemGroups[it.id] ?: return@mapNotNull null
                val firstItem = items.firstOrNull() ?: return@mapNotNull null
                val menuName = firstItem.toMenuNameBySize(items.size)
                OrderResponses.from(it, store, menuName)
            }
        }
    }

}
