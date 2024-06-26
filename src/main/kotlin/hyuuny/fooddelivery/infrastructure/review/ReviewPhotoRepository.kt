package hyuuny.fooddelivery.infrastructure.review

import hyuuny.fooddelivery.domain.review.ReviewPhoto

interface ReviewPhotoRepository {

    suspend fun insertAll(reviewPhotos: List<ReviewPhoto>): List<ReviewPhoto>

    suspend fun findAllByReviewIdIn(reviewIds: List<Long>): List<ReviewPhoto>

    suspend fun findAllByReviewId(reviewId: Long): List<ReviewPhoto>

    suspend fun deleteAllByReviewId(reviewId: Long)

}
