package hyuuny.fooddelivery.orders.infrastructure

import hyuuny.fooddelivery.orders.domain.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderDao : CoroutineCrudRepository<Order, Long> {

    suspend fun findByIdAndUserId(id: Long, userId: Long): Order?

}
