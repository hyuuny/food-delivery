package hyuuny.fooddelivery.infrastructure.order

import hyuuny.fooddelivery.domain.order.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderDao : CoroutineCrudRepository<Order, Long> {

    suspend fun findByIdAndUserId(id: Long, userId: Long): Order?

}
