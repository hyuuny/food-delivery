package hyuuny.fooddelivery.reviews.infrastructure

import hyuuny.fooddelivery.reviews.domain.Review
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReviewDao : CoroutineCrudRepository<Review, Long> {

    suspend fun findAllByUserIdIn(userIds: List<Long>): List<Review>

    suspend fun existsByUserIdAndOrderId(userId: Long, orderId: Long): Boolean

}
