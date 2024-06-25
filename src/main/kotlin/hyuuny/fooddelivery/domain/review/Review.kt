package hyuuny.fooddelivery.domain.review

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

}