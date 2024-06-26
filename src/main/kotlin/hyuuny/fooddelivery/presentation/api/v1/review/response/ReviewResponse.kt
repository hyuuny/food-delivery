package hyuuny.fooddelivery.presentation.api.v1.review.response

import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.review.ReviewPhoto
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.user.User
import java.time.LocalDateTime

data class ReviewResponse(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val orderId: Long,
    val storeName: String,
    val userNickname: String,
    val userImageUrl: String? = null,
    val score: Int,
    val content: String,
    val photos: List<ReviewPhotoResponse>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Review, user: User, store: Store, photos: List<ReviewPhoto>): ReviewResponse =
            ReviewResponse(
                id = entity.id!!,
                userId = entity.userId,
                storeId = entity.storeId,
                orderId = entity.orderId,
                storeName = store.name,
                userNickname = user.nickname,
                userImageUrl = user.imageUrl,
                score = entity.score,
                content = entity.content,
                photos = photos.map { ReviewPhotoResponse.from(it) },
                createdAt = entity.createdAt,
            )
    }
}

data class ReviewPhotoResponse(
    val id: Long,
    val reviewId: Long,
    val photoUrl: String,
    val createdAt: String,
) {
    companion object {
        fun from(entity: ReviewPhoto): ReviewPhotoResponse = ReviewPhotoResponse(
            id = entity.id!!,
            reviewId = entity.reviewId,
            photoUrl = entity.photoUrl,
            createdAt = entity.createdAt.toString(),
        )
    }
}
