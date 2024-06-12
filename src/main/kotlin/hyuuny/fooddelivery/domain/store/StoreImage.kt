package hyuuny.fooddelivery.domain.store

import CreateStoreImageCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("store_images")
class StoreImage(
    id: Long? = null,
    val storeId: Long,
    val imageUrl: String,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

    companion object {
        fun handle(command: CreateStoreImageCommand): StoreImage = StoreImage(
            storeId = command.storeId,
            imageUrl = command.imageUrl,
            createdAt = command.createdAt,
        )
    }

}