package hyuuny.fooddelivery.domain.reviewcomment

import hyuuny.fooddelivery.application.reviewcomment.command.ChangeReviewCommentContentCommand
import hyuuny.fooddelivery.application.reviewcomment.command.CreateReviewCommentCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("review_comments")
class ReviewComment(
    id: Long? = null,
    val userId: Long,
    val reviewId: Long,
    content: String,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var content = content
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateReviewCommentCommand): ReviewComment = ReviewComment(
            userId = command.userId,
            reviewId = command.reviewId,
            content = command.content,
            createdAt = command.createdAt,
            updatedAt = command.createdAt,
        )
    }

    fun handle(command: ChangeReviewCommentContentCommand) {
        this.content = command.content
        this.updatedAt = command.updatedAt
    }

    fun getOwnerName(): String = "사장님"

    fun getOwnerImageUrl(): String =
        "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/owner-default.jpeg"

}
