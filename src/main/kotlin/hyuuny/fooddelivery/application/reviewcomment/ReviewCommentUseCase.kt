package hyuuny.fooddelivery.application.reviewcomment

import AdminReviewCommentSearchCondition
import ChangeContentRequest
import CreateReviewCommentRequest
import hyuuny.fooddelivery.application.reviewcomment.command.ChangeReviewCommentContentCommand
import hyuuny.fooddelivery.application.reviewcomment.command.CreateReviewCommentCommand
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.reviewcomment.ReviewCommentRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
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

    @Transactional
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

    suspend fun getReviewComment(id: Long): ReviewComment = findReviewCommentByIdOrThrow(id)

    @Transactional
    suspend fun changeContent(id: Long, request: ChangeContentRequest) {
        val reviewComment = findReviewCommentByIdOrThrow(id)

        val now = LocalDateTime.now()
        reviewComment.handle(
            ChangeReviewCommentContentCommand(
                content = request.content,
                updatedAt = now
            )
        )
        repository.updateContent(reviewComment)
    }

    @Transactional
    suspend fun deleteReviewComment(id: Long) {
        val reviewComment = findReviewCommentByIdOrThrow(id)
        repository.delete(reviewComment.id!!)
    }

    private suspend fun findReviewCommentByIdOrThrow(id: Long) =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 리뷰 댓글을 찾을 수 없습니다.")

}
