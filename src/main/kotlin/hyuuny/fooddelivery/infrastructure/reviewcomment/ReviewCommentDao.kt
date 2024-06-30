package hyuuny.fooddelivery.infrastructure.reviewcomment

import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReviewCommentDao : CoroutineCrudRepository<ReviewComment, Long> {
}
