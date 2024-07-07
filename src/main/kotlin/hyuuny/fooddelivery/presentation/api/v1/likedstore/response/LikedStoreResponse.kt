package hyuuny.fooddelivery.presentation.api.v1.likedstore.response

import hyuuny.fooddelivery.domain.likedstore.LikedStore
import hyuuny.fooddelivery.domain.store.Store
import java.time.LocalDateTime

data class LikedStoreResponses(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val storeName: String,
    val storeDescription: String,
    val storeMinimumOrderAmount: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: LikedStore, store: Store) = LikedStoreResponses(
            id = entity.id!!,
            userId = entity.userId,
            storeId = entity.storeId,
            storeName = store.name,
            storeDescription = store.description,
            storeMinimumOrderAmount = store.minimumOrderAmount,
            createdAt = entity.createdAt,
        )
    }

}
