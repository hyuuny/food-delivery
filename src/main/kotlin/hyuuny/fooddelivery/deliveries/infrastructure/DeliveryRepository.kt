package hyuuny.fooddelivery.deliveries.infrastructure

import ApiDeliverSearchCondition
import hyuuny.fooddelivery.deliveries.domain.Delivery
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface DeliveryRepository {

    suspend fun insert(delivery: Delivery): Delivery

    suspend fun findById(id: Long): Delivery?

    suspend fun findAllDeliveries(searchCondition: ApiDeliverSearchCondition, pageable: Pageable): PageImpl<Delivery>

    suspend fun updatePickupTime(delivery: Delivery)

    suspend fun updateDeliveredTime(delivery: Delivery)

    suspend fun updateCancelTime(delivery: Delivery)

    suspend fun updateStatus(delivery: Delivery)

}
