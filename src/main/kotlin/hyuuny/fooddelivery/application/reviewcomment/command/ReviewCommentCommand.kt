import java.time.LocalDateTime

data class CreateReviewCommentCommand(
    val userId: Long,
    val reviewId: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
