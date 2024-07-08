import java.time.LocalDateTime

data class CreateReviewCommand(
    val userId: Long,
    val storeId: Long,
    val orderId: Long,
    val score: Int,
    val content: String,
    val createdAt: LocalDateTime,
)

data class CreateReviewPhotoCommand(
    val reviewId: Long,
    val photoUrl: String,
    val createdAt: LocalDateTime,
)
