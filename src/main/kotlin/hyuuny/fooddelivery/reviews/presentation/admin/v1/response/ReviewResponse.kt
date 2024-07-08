package hyuuny.fooddelivery.reviews.presentation.admin.v1.response

import hyuuny.fooddelivery.orders.domain.OrderItem
import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.reviews.domain.ReviewPhoto
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.users.domain.User
import java.time.LocalDateTime

data class ReviewResponses(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val orderId: Long,
    val storeName: String,
    val userName: String,
    val userNickname: String,
    val userPhoneNumber: String,
    val score: Int,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Review, user: User, store: Store): ReviewResponses = ReviewResponses(
            id = entity.id!!,
            userId = entity.userId,
            storeId = entity.storeId,
            orderId = entity.orderId,
            storeName = store.name,
            userName = user.name,
            userNickname = user.nickname,
            userPhoneNumber = user.phoneNumber,
            score = entity.score,
            createdAt = entity.createdAt,
        )
    }
}

data class ReviewResponse(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val orderId: Long,
    val storeName: String,
    val userName: String,
    val userNickname: String,
    val userImageUrl: String? = null,
    val score: Int,
    val content: String,
    val items: List<ReviewItemResponse>,
    val photos: List<ReviewPhotoResponse>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            entity: Review,
            user: User,
            store: Store,
            items: List<OrderItem>,
            photos: List<ReviewPhoto>
        ): ReviewResponse =
            ReviewResponse(
                id = entity.id!!,
                userId = entity.userId,
                storeId = entity.storeId,
                orderId = entity.orderId,
                storeName = store.name,
                userName = user.name,
                userNickname = user.nickname,
                userImageUrl = user.imageUrl,
                score = entity.score,
                content = entity.content,
                items = items.map { ReviewItemResponse.from(it) },
                photos = photos.map { ReviewPhotoResponse.from(it) },
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
