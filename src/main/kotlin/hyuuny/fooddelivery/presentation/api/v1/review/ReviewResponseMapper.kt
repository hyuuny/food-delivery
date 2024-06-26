package hyuuny.fooddelivery.presentation.api.v1.review

import hyuuny.fooddelivery.application.review.ReviewPhotoUseCase
import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.presentation.api.v1.review.response.ReviewResponse
import hyuuny.fooddelivery.presentation.api.v1.review.response.ReviewResponses
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class ReviewResponseMapper(
    private val useCase: ReviewUseCase,
    private val reviewPhotoUseCase: ReviewPhotoUseCase,
    private val userUseCase: UserUseCase,
    private val storeUseCase: StoreUseCase,
) {

    suspend fun mapToReviewResponse(review: Review) = coroutineScope {
        val reviewPhotosDeferred = async { reviewPhotoUseCase.getAllByReviewId(review.id!!) }
        val userDeferred = async { userUseCase.getUser(review.userId) }
        val storeDeferred = async { storeUseCase.getStore(review.storeId) }

        val reviewPhotos = reviewPhotosDeferred.await().sortedBy { it.id }
        val user = userDeferred.await()
        val store = storeDeferred.await()

        ReviewResponse.from(review, user, store, reviewPhotos)
    }

    suspend fun mapToReviewResponses(reviews: List<Review>) = coroutineScope {
        val reviewIds = reviews.mapNotNull { it.id }
        val userIds = reviews.map { it.userId }
        val storeIds = reviews.map { it.storeId }

        val reviewPhotosDeferred = async { reviewPhotoUseCase.getAllByReviewIdIn(reviewIds) }
        val userDeferred = async { userUseCase.getAllByIds(userIds) }
        val storeDeferred = async { storeUseCase.getAllByIds(storeIds) }
        val userReviewsDeferred = async { useCase.getAllByUserIds(userIds) }

        val reviewPhotos = reviewPhotosDeferred.await().sortedBy { it.id }
        val user = userDeferred.await()
        val store = storeDeferred.await()
        val userReviews = userReviewsDeferred.await()

        val reviewPhotoGroup = reviewPhotos.groupBy { it.reviewId }
        val userMap = user.associateBy { it.id }
        val storeMap = store.associateBy { it.id }
        val userReviewGroup = userReviews.groupBy { it.userId }

        reviews.mapNotNull {
            val user = userMap[it.userId] ?: return@mapNotNull null
            val store = storeMap[it.storeId] ?: return@mapNotNull null
            val reviewPhotos = reviewPhotoGroup[it.id] ?: return@mapNotNull null
            val userReviews = userReviewGroup[it.userId] ?: return@mapNotNull null

            val totalScore = userReviews.sumOf { it.score }.toDouble()
            val reviewCount = userReviews.count()
            val averageScore = if (reviewCount > 0) totalScore / reviewCount else 0.0

            ReviewResponses.from(it, user, store, averageScore, reviewCount, reviewPhotos)
        }
    }

}
