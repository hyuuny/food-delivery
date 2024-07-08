package hyuuny.fooddelivery.orders.presentation.api.v1

import hyuuny.fooddelivery.orders.application.OrderItemOptionUseCase
import hyuuny.fooddelivery.orders.application.OrderItemUseCase
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.orders.presentation.api.v1.response.OrderItemOptionResponse
import hyuuny.fooddelivery.orders.presentation.api.v1.response.OrderItemResponse
import hyuuny.fooddelivery.orders.presentation.api.v1.response.OrderResponse
import hyuuny.fooddelivery.orders.presentation.api.v1.response.OrderResponses
import hyuuny.fooddelivery.stores.application.StoreUseCase
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
