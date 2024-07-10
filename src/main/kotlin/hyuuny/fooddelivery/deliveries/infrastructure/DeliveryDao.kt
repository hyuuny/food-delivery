package hyuuny.fooddelivery.deliveries.infrastructure

import hyuuny.fooddelivery.deliveries.domain.Delivery
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface DeliveryDao: CoroutineCrudRepository<Delivery, Long> {

    suspend fun findByOrderId(orderId: Long): Delivery?

    suspend fun findAllByRiderId(riderId: Long): List<Delivery>

}
