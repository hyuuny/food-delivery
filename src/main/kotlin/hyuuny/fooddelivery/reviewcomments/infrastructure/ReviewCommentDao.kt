package hyuuny.fooddelivery.reviewcomments.infrastructure

import hyuuny.fooddelivery.reviewcomments.domain.ReviewComment
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReviewCommentDao : CoroutineCrudRepository<ReviewComment, Long> {

    suspend fun findAllByReviewIdIn(reviewIds: List<Long>): List<ReviewComment>

}
