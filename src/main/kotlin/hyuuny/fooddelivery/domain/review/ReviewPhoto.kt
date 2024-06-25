package hyuuny.fooddelivery.domain.review

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

}