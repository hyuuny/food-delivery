package hyuuny.fooddelivery.domain.reviewcomment

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

}
