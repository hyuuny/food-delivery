package hyuuny.fooddelivery.reviewcomments.application.command

import java.time.LocalDateTime

data class CreateReviewCommentCommand(
    val userId: Long,
    val reviewId: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class ChangeReviewCommentContentCommand(
    val content: String,
    val updatedAt: LocalDateTime,
)
