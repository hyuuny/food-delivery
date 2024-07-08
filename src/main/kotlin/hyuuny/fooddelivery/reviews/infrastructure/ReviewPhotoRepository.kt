package hyuuny.fooddelivery.reviews.infrastructure

import hyuuny.fooddelivery.reviews.domain.ReviewPhoto

interface ReviewPhotoRepository {

    suspend fun insertAll(reviewPhotos: List<ReviewPhoto>): List<ReviewPhoto>

    suspend fun findAllByReviewIdIn(reviewIds: List<Long>): List<ReviewPhoto>

    suspend fun findAllByReviewId(reviewId: Long): List<ReviewPhoto>

    suspend fun deleteAllByReviewId(reviewId: Long)

}
