package hyuuny.fooddelivery.likedstores.presentation.api.v1.response

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.likedstores.domain.LikedStore
import hyuuny.fooddelivery.stores.domain.Store
import java.time.LocalDateTime

data class LikedStoreResponses(
    val id: Long,
    val userId: Long,
    val storeId: Long,
    val deliveryType: DeliveryType,
    val storeName: String,
    val storeDescription: String,
    val storeMinimumOrderAmount: Long,
    val averageScore: Double,
    val deliveryFee: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: LikedStore, store: Store, averageScore: Double) = LikedStoreResponses(
            id = entity.id!!,
            userId = entity.userId,
            storeId = entity.storeId,
            deliveryType = store.deliveryType,
            storeName = store.name,
            storeDescription = store.description,
            storeMinimumOrderAmount = store.minimumOrderAmount,
            averageScore = averageScore,
            deliveryFee = store.deliveryFee,
            createdAt = entity.createdAt,
        )
    }

}
