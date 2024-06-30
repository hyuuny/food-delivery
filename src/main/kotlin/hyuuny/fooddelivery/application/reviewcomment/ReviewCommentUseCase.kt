package hyuuny.fooddelivery.application.reviewcomment

import AdminReviewCommentSearchCondition
import CreateReviewCommentCommand
import CreateReviewCommentRequest
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.reviewcomment.ReviewCommentRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReviewCommentUseCase(
    private val repository: ReviewCommentRepository,
) {

    suspend fun getReviewCommentByAdminCondition(
        searchCondition: AdminReviewCommentSearchCondition,
        pageable: Pageable,
    ): PageImpl<ReviewComment> {
        val page = repository.findAllReviewComments(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun createReviewComment(
        request: CreateReviewCommentRequest,
        getOwner: suspend (userId: Long) -> User,
        getReview: suspend (reviewId: Long) -> Review,
    ): ReviewComment {
        val now = LocalDateTime.now()
        val user = getOwner(request.userId)
        val review = getReview(request.reviewId)
        val reviewComment = ReviewComment.handle(
            CreateReviewCommentCommand(
                reviewId = review.id!!,
                userId = user.id!!,
                content = request.content,
                createdAt = now,
                updatedAt = now,
            )
        )
        return repository.insert(reviewComment)
    }

}
