package hyuuny.fooddelivery.deliveries.presentation.admin.v1

import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.deliveries.presentation.admin.v1.response.DeliveryResponse
import hyuuny.fooddelivery.deliveries.presentation.admin.v1.response.DeliveryResponses
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.stores.application.StoreDetailUseCase
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.users.application.UserUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component("adminDeliveryResponseMapper")
class DeliveryResponseMapper(
    private val userUseCase: UserUseCase,
    private val orderUseCase: OrderUseCase,
    private val storeUseCase: StoreUseCase,
    private val storeDetailUseCase: StoreDetailUseCase,
) {

    suspend fun mapToDeliveryResponses(deliveries: List<Delivery>) = coroutineScope {
        val orderIds = deliveries.map { it.orderId }
        val riderUserIds = deliveries.map { it.riderId }

        val orderDeferred = async { orderUseCase.getAllByIds(orderIds) }
        val orders = orderDeferred.await()

        val orderStoreIds = orders.map { it.storeId }
        val orderUserIds = orders.map { it.userId }
        val storeDeferred = async { storeUseCase.getAllByIds(orderStoreIds) }
        val riderDeferred = async { userUseCase.getAllByIds(riderUserIds) }
        val userDeferred = async { userUseCase.getAllByIds(orderUserIds) }

        val stores = storeDeferred.await()
        val riders = riderDeferred.await()
        val users = userDeferred.await()

        val orderMap = orders.associateBy { it.id }
        val storeMap = stores.associateBy { it.id }
        val riderMap = riders.associateBy { it.id }
        val userMap = users.associateBy { it.id }

        deliveries.mapNotNull {
            val order = orderMap[it.orderId] ?: return@mapNotNull null
            val store = storeMap[order.storeId] ?: return@mapNotNull null
            val rider = riderMap[it.riderId] ?: return@mapNotNull null
            val user = userMap[order.userId] ?: return@mapNotNull null
            DeliveryResponses.from(it, rider, order, store, user)
        }
    }

    suspend fun mapToDeliveryResponse(delivery: Delivery) = coroutineScope {
        val orderDeferred = async { orderUseCase.getOrder(delivery.orderId) }
        val order = orderDeferred.await()

        val storeDeferred = async { storeUseCase.getStore(order.storeId) }
        val storeDetailDeferred = async { storeDetailUseCase.getStoreDetailByStoreId(order.storeId) }
        val riderDeferred = async { userUseCase.getUser(delivery.riderId) }
        val userDeferred = async { userUseCase.getUser(order.userId) }

        val store = storeDeferred.await()
        val storeDetail = storeDetailDeferred.await()
        val rider = riderDeferred.await()
        val user = userDeferred.await()

        DeliveryResponse.from(delivery, rider, order, store, storeDetail, user)
    }

}
