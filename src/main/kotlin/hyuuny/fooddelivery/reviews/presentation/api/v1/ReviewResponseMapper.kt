package hyuuny.fooddelivery.reviews.presentation.api.v1

import hyuuny.fooddelivery.orders.application.OrderItemUseCase
import hyuuny.fooddelivery.reviewcomments.application.ReviewCommentUseCase
import hyuuny.fooddelivery.reviews.application.ReviewPhotoUseCase
import hyuuny.fooddelivery.reviews.application.ReviewUseCase
import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.reviews.presentation.api.v1.response.ReviewResponse
import hyuuny.fooddelivery.reviews.presentation.api.v1.response.ReviewResponses
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.users.application.UserUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class ReviewResponseMapper(
    private val useCase: ReviewUseCase,
    private val reviewPhotoUseCase: ReviewPhotoUseCase,
    private val userUseCase: UserUseCase,
    private val storeUseCase: StoreUseCase,
    private val orderItemUseCase: OrderItemUseCase,
    private val reviewCommentUseCase: ReviewCommentUseCase,
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
        val orderIds = reviews.map { it.orderId }

        val reviewPhotosDeferred = async { reviewPhotoUseCase.getAllByReviewIdIn(reviewIds) }
        val userDeferred = async { userUseCase.getAllByIds(userIds) }
        val storeDeferred = async { storeUseCase.getAllByIds(storeIds) }
        val userReviewsDeferred = async { useCase.getAllByUserIds(userIds) }
        val orderItemsDeferred = async { orderItemUseCase.getAllByOrderIdIn(orderIds) }
        val reviewCommentsDeferred = async { reviewCommentUseCase.getAllByReviewIds(reviewIds) }

        val reviewPhotos = reviewPhotosDeferred.await().sortedBy { it.id }
        val user = userDeferred.await()
        val store = storeDeferred.await()
        val userReviews = userReviewsDeferred.await()
        val orderItems = orderItemsDeferred.await()
        val reviewComments = reviewCommentsDeferred.await()

        val reviewPhotoGroup = reviewPhotos.groupBy { it.reviewId }
        val userMap = user.associateBy { it.id }
        val storeMap = store.associateBy { it.id }
        val userReviewGroup = userReviews.groupBy { it.userId }
        val reviewItemGroup = orderItems.groupBy { it.orderId }
        val commentMap = reviewComments.associateBy { it.reviewId }

        reviews.mapNotNull {
            val user = userMap[it.userId] ?: return@mapNotNull null
            val store = storeMap[it.storeId] ?: return@mapNotNull null
            val reviewPhotos = reviewPhotoGroup[it.id] ?: return@mapNotNull null
            val userReviews = userReviewGroup[it.userId] ?: return@mapNotNull null
            val items = reviewItemGroup[it.orderId] ?: return@mapNotNull null
            val reviewComment = commentMap[it.id]

            val totalScore = userReviews.sumOf { it.score }.toDouble()
            val reviewCount = userReviews.count()
            val averageScore = if (reviewCount > 0) totalScore / reviewCount else 0.0

            ReviewResponses.from(it, user, store, averageScore, reviewCount, items, reviewPhotos, reviewComment)
        }
    }

}
