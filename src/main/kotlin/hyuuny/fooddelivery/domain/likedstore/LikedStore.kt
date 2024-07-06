package hyuuny.fooddelivery.domain.likedstore

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("liked_stores")
class LikedStore(
    id: Long? = null,
    val userId: Long,
    val storeId: Long,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

}
