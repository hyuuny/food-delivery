package hyuuny.fooddelivery.presentation.admin.v1.order

import hyuuny.fooddelivery.application.order.OrderItemOptionUseCase
import hyuuny.fooddelivery.application.order.OrderItemUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.presentation.admin.v1.order.response.OrderItemOptionResponse
import hyuuny.fooddelivery.presentation.admin.v1.order.response.OrderItemResponse
import hyuuny.fooddelivery.presentation.admin.v1.order.response.OrderResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component(value = "adminOrderResponseMapper")
class OrderResponseMapper(
    private val orderItemUseCase: OrderItemUseCase,
    private val orderItemOptionUseCase: OrderItemOptionUseCase,
    private val userUseCase: UserUseCase,
    private val storeUseCase: StoreUseCase,
) {

    suspend fun mapToOrderResponse(order: Order): OrderResponse = coroutineScope {
        val orderItemsDeferred = async { orderItemUseCase.getAllByOrderId(order.id!!) }
        val userDeferred = async { userUseCase.getUser(order.userId) }
        val storeDeferred = async { storeUseCase.getStore(order.storeId) }

        val orderItems = orderItemsDeferred.await()
        val orderItemOptionGroupDeferred = async {
            orderItemOptionUseCase.getAllByOrderItemIdIn(orderItems.mapNotNull { it.id }).groupBy { it.orderItemId }
        }

        val orderItemOptionGroup = orderItemOptionGroupDeferred.await()
        val user = userDeferred.await()
        val store = storeDeferred.await()

        val orderItemResponses = orderItems.mapNotNull { orderItem ->
            val optionsOfOrderItem = orderItemOptionGroup[orderItem.id] ?: return@mapNotNull null
            val orderItemOptionResponses = optionsOfOrderItem.map { orderItemOption ->
                OrderItemOptionResponse.from(orderItemOption)
            }
            OrderItemResponse.from(orderItem, orderItemOptionResponses)
        }
        OrderResponse.from(order, user.name, store.name, orderItemResponses)
    }

}
