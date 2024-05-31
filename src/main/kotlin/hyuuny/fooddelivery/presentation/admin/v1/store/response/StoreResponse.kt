package hyuuny.fooddelivery.presentation.admin.v1.store.response

import hyuuny.fooddelivery.domain.store.DeliveryType
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.store.StoreDetail
import hyuuny.fooddelivery.domain.store.StoreImage
import java.time.LocalDateTime

data class StoreResponse(
    val id: Long,
    val categoryId: Long,
    val deliveryType: DeliveryType,
    val name: String,
    val ownerName: String,
    val taxId: String,
    val deliveryFee: Long,
    val minimumOrderAmount: Long,
    val iconImageUrl: String?,
    val description: String,
    val foodOrigin: String,
    val phoneNumber: String,
    val storeDetail: StoreDetailResponse,
    val storeImages: List<StoreImageResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(
            entity: Store,
            storeDetail: StoreDetailResponse,
            storeImages: List<StoreImageResponse>
        ): StoreResponse {
            return StoreResponse(
                id = entity.id!!,
                categoryId = entity.categoryId,
                deliveryType = entity.deliveryType,
                name = entity.name,
                ownerName = entity.ownerName,
                taxId = entity.taxId,
                deliveryFee = entity.deliveryFee,
                minimumOrderAmount = entity.minimumOrderAmount,
                iconImageUrl = entity.iconImageUrl,
                description = entity.description,
                foodOrigin = entity.foodOrigin,
                phoneNumber = entity.phoneNumber,
                storeDetail = storeDetail,
                storeImages = storeImages,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
        }
    }
}

data class StoreDetailResponse(
    val id: Long,
    val storeId: Long,
    val zipCode: String,
    val address: String,
    val detailedAddress: String?,
    val openHours: String?,
    val closedDay: String?,
) {
    companion object {
        fun from(entity: StoreDetail): StoreDetailResponse {
            return StoreDetailResponse(
                id = entity.id!!,
                storeId = entity.storeId,
                zipCode = entity.zipCode,
                address = entity.address,
                detailedAddress = entity.detailedAddress,
                openHours = entity.openHours,
                closedDay = entity.closedDay ?: "연중무휴",
            )
        }
    }
}

data class StoreImageResponse(
    val id: Long,
    val storeId: Long,
    val imageUrl: String,
) {
    companion object {
        fun from(entity: StoreImage): StoreImageResponse {
            return StoreImageResponse(
                id = entity.id!!,
                storeId = entity.storeId,
                imageUrl = entity.imageUrl,
            )
        }
    }
}
