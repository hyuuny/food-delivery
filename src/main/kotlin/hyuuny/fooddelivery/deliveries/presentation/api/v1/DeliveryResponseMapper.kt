package hyuuny.fooddelivery.deliveries.presentation.api.v1

import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.deliveries.presentation.api.v1.response.DeliveryDetailResponses
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.stores.application.StoreUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class DeliveryResponseMapper(
    private val orderUseCase: OrderUseCase,
    private val storeUseCase: StoreUseCase,
) {

    suspend fun mapToDeliveryResponses(deliveries: List<Delivery>) = coroutineScope {
        val orderDeferred = async { orderUseCase.getAllByIds(deliveries.map { it.riderId }) }
        val orders = orderDeferred.await().sortedByDescending { it.id }

        val storeDeferred = async { storeUseCase.getAllByIds(orders.map { it.storeId }) }
        val stores = storeDeferred.await()

        val orderMap = orders.associateBy { it.id }
        val storeMap = stores.associateBy { it.id }

        deliveries.mapNotNull {
            val order = orderMap[it.orderId] ?: return@mapNotNull null
            val store = storeMap[order.storeId] ?: return@mapNotNull null
            DeliveryDetailResponses.from(it, order, store)
        }
    }

}