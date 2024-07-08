package hyuuny.fooddelivery.reviewcomments.infrastructure

import AdminReviewCommentSearchCondition
import hyuuny.fooddelivery.reviewcomments.domain.ReviewComment
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface ReviewCommentRepository {

    suspend fun insert(reviewComment: ReviewComment): ReviewComment

    suspend fun findById(id: Long): ReviewComment?

    suspend fun findAllReviewComments(
        searchCondition: AdminReviewCommentSearchCondition,
        pageable: Pageable
    ): PageImpl<ReviewComment>

    suspend fun updateContent(reviewComment: ReviewComment)

    suspend fun delete(id: Long)

    suspend fun findAllByReviewIdIn(reviewIds: List<Long>): List<ReviewComment>

}
