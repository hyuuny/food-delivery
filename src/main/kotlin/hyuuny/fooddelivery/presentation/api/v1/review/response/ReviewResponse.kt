package hyuuny.fooddelivery.presentation.api.v1.review.response

import hyuuny.fooddelivery.domain.order.OrderItem
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.review.ReviewPhoto
import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
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

data class ReviewResponses(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val orderId: Long,
    val storeName: String,
    val userNickname: String,
    val userImageUrl: String? = null,
    val score: Int,
    val averageScore: Double,
    val reviewCount: Int,
    val content: String,
    val items: List<ReviewItemResponse>,
    val photos: List<ReviewPhotoResponses>,
    val comment: ReviewCommentResponse?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            entity: Review,
            user: User,
            store: Store,
            averageScore: Double = 0.0,
            reviewCount: Int,
            items: List<OrderItem>,
            photos: List<ReviewPhoto>,
            comment: ReviewComment?,
        ): ReviewResponses =
            ReviewResponses(
                id = entity.id!!,
                userId = entity.userId,
                storeId = entity.storeId,
                orderId = entity.orderId,
                storeName = store.name,
                userNickname = user.nickname,
                userImageUrl = user.imageUrl,
                score = entity.score,
                averageScore = averageScore,
                reviewCount = reviewCount,
                content = entity.content,
                items = items.map { ReviewItemResponse.from(it) },
                photos = photos.map { ReviewPhotoResponses.from(it) },
                comment = comment?.let { ReviewCommentResponse.from(it) },
                createdAt = entity.createdAt,
            )
    }
}

data class ReviewItemResponse(
    val orderId: Long,
    val menuName: String,
) {
    companion object {
        fun from(entity: OrderItem): ReviewItemResponse =
            ReviewItemResponse(
                orderId = entity.orderId,
                menuName = entity.menuName,
            )
    }
}

data class ReviewPhotoResponses(
    val id: Long,
    val reviewId: Long,
    val photoUrl: String,
) {
    companion object {
        fun from(entity: ReviewPhoto): ReviewPhotoResponses = ReviewPhotoResponses(
            id = entity.id!!,
            reviewId = entity.reviewId,
            photoUrl = entity.photoUrl
        )
    }
}

data class ReviewCommentResponse(
    val id: Long,
    val reviewId: Long,
    val userId: Long,
    val ownerName: String,
    val ownerImageUrl: String,
    val content: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: ReviewComment): ReviewCommentResponse =
            ReviewCommentResponse(
                id = entity.id!!,
                reviewId = entity.reviewId,
                userId = entity.userId,
                ownerName = entity.getOwnerName(),
                ownerImageUrl = entity.getOwnerImageUrl(),
                content = entity.content,
                createdAt = entity.createdAt,
            )
    }
}