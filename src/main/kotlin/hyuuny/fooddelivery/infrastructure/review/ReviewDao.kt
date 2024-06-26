package hyuuny.fooddelivery.infrastructure.review

import hyuuny.fooddelivery.domain.review.Review
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReviewDao : CoroutineCrudRepository<Review, Long> {

    suspend fun findAllByUserIdIn(userIds: List<Long>): List<Review>

}
