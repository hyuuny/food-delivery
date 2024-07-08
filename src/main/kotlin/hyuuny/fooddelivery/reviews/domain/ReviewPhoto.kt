package hyuuny.fooddelivery.reviews.domain

import CreateReviewPhotoCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("review_photos")
class ReviewPhoto(
    id: Long? = null,
    val reviewId: Long,
    val photoUrl: String,
    val createdAt: LocalDateTime,
) {

    @Id
    var id: Long? = id
        protected set

    companion object {
        fun handle(command: CreateReviewPhotoCommand): ReviewPhoto = ReviewPhoto(
            reviewId = command.reviewId,
            photoUrl = command.photoUrl,
            createdAt = command.createdAt,
        )
    }

}
