package hyuuny.fooddelivery.reviews.presentation.admin.v1

import hyuuny.fooddelivery.orders.application.OrderItemUseCase
import hyuuny.fooddelivery.reviews.application.ReviewPhotoUseCase
import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.reviews.presentation.admin.v1.response.ReviewResponse
import hyuuny.fooddelivery.reviews.presentation.admin.v1.response.ReviewResponses
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.users.application.UserUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component(value = "adminReviewResponseMapper")
class ReviewResponseMapper(
    private val reviewPhotoUseCase: ReviewPhotoUseCase,
    private val userUseCase: UserUseCase,
    private val storeUseCase: StoreUseCase,
    private val orderItemUseCase: OrderItemUseCase,
) {

    suspend fun mepToReviewResponses(reviews: List<Review>) = coroutineScope {
        val userIds = reviews.map { it.userId }
        val storeIds = reviews.map { it.storeId }

        val userDeferred = async { userUseCase.getAllByIds(userIds) }
        val storeDeferred = async { storeUseCase.getAllByIds(storeIds) }

        val user = userDeferred.await()
        val store = storeDeferred.await()

        val userMap = user.associateBy { it.id }
        val storeMap = store.associateBy { it.id }

        reviews.mapNotNull {
            val user = userMap[it.userId] ?: return@mapNotNull null
            val store = storeMap[it.storeId] ?: return@mapNotNull null
            ReviewResponses.from(it, user, store)
        }
    }

    suspend fun mapToReviewResponse(review: Review) = coroutineScope {
        val reviewPhotosDeferred = async { reviewPhotoUseCase.getAllByReviewId(review.id!!) }
        val userDeferred = async { userUseCase.getUser(review.userId) }
        val storeDeferred = async { storeUseCase.getStore(review.storeId) }
        val orderItemsDeferred = async { orderItemUseCase.getAllByOrderId(review.orderId) }

        val reviewPhotos = reviewPhotosDeferred.await().sortedBy { it.id }
        val user = userDeferred.await()
        val store = storeDeferred.await()
        val orderItems = orderItemsDeferred.await()

        ReviewResponse.from(review, user, store, orderItems, reviewPhotos)
    }

}
