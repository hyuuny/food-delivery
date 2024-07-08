package hyuuny.fooddelivery.likedstores.domain

import LikeOrCancelCommand
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

    companion object {
        fun handle(command: LikeOrCancelCommand): LikedStore = LikedStore(
            userId = command.userId,
            storeId = command.storeId,
            createdAt = command.createdAt,
        )
    }

}
