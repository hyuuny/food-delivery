package hyuuny.fooddelivery.deliveries.infrastructure

import hyuuny.fooddelivery.deliveries.domain.Delivery

interface DeliveryRepository {

    suspend fun insert(delivery: Delivery): Delivery

    suspend fun findById(id: Long): Delivery?

    suspend fun findAllByRiderId(riderId: Long): List<Delivery>

    suspend fun updatePickupTime(delivery: Delivery)

    suspend fun updateDeliveredTime(delivery: Delivery)

    suspend fun updateCancelTime(delivery: Delivery)

    suspend fun updateStatus(delivery: Delivery)

}
