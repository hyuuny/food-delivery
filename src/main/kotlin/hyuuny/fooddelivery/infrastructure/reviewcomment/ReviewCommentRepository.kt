package hyuuny.fooddelivery.infrastructure.reviewcomment

import AdminReviewCommentSearchCondition
import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface ReviewCommentRepository {

    suspend fun insert(reviewComment: ReviewComment): ReviewComment

    suspend fun findById(id: Long): ReviewComment?

    suspend fun findAllReviewComments(
        searchCondition: AdminReviewCommentSearchCondition,
        pageable: Pageable
    ): PageImpl<ReviewComment>

    suspend fun update(reviewComment: ReviewComment)

    suspend fun delete(id: Long)

}
