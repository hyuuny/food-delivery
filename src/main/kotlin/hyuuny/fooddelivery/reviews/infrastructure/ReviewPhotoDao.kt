package hyuuny.fooddelivery.reviews.infrastructure

import hyuuny.fooddelivery.reviews.domain.ReviewPhoto
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReviewPhotoDao : CoroutineCrudRepository<ReviewPhoto, Long> {

    suspend fun findAllByReviewIdIn(reviewIds: List<Long>): List<ReviewPhoto>

    suspend fun findAllByReviewId(reviewId: Long): List<ReviewPhoto>

}
