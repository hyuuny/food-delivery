package hyuuny.fooddelivery.presentation.admin.v1.reviewcomment.response

import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import java.time.LocalDateTime

data class ReviewCommentResponse(
    val id: Long,
    val userId: Long,
    val reviewId: Long,
    val ownerName: String,
    val ownerImageUrl: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: ReviewComment): ReviewCommentResponse = ReviewCommentResponse(
            id = entity.id!!,
            userId = entity.userId,
            reviewId = entity.reviewId,
            ownerName = entity.getOwnerName(),
            ownerImageUrl = entity.getOwnerImageUrl(),
            content = entity.content,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}

data class ReviewCommentResponses(
    val id: Long,
    val userId: Long,
    val reviewId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: ReviewComment): ReviewCommentResponses = ReviewCommentResponses(
            id = entity.id!!,
            userId = entity.userId,
            reviewId = entity.reviewId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}
