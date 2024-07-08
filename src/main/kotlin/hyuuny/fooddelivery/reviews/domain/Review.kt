package hyuuny.fooddelivery.reviews.domain

import CreateReviewCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("reviews")
class Review(
    id: Long? = null,
    val userId: Long,
    val storeId: Long,
    val orderId: Long,
    val score: Int,
    val content: String,
    val createdAt: LocalDateTime,
) {

    @Id
    var id: Long? = id
        protected set

    companion object {
        fun handle(command: CreateReviewCommand): Review = Review(
            userId = command.userId,
            storeId = command.storeId,
            orderId = command.orderId,
            score = command.score,
            content = command.content,
            createdAt = command.createdAt,
        )
    }

}
