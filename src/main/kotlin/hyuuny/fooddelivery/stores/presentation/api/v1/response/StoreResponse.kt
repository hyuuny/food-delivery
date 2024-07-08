package hyuuny.fooddelivery.stores.presentation.api.v1.response

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.menugroups.domain.MenuGroup
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.domain.StoreDetail
import hyuuny.fooddelivery.stores.domain.StoreImage
import java.time.LocalDateTime

class StoreResponse(
    val id: Long,
    val categoryId: Long,
    val deliveryType: DeliveryType,
    val name: String,
    val ownerName: String,
    val taxId: String,
    val deliveryFee: Long,
    val minimumOrderAmount: Long,
    val averageScore: Double,
    val iconImageUrl: String?,
    val description: String,
    val foodOrigin: String,
    val phoneNumber: String,
    val storeDetail: StoreDetailResponse,
    val storeImages: List<StoreImageResponse>?,
    val menuGroups: List<MenuGroupResponse>?,
) {
    companion object {
        fun from(
            entity: Store,
            averageScore: Double,
            storeDetail: StoreDetailResponse,
            storeImages: List<StoreImageResponse>?,
            menuGroups: List<MenuGroupResponse>?,
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
                averageScore = averageScore,
                iconImageUrl = entity.iconImageUrl,
                description = entity.description,
                foodOrigin = entity.foodOrigin,
                phoneNumber = entity.phoneNumber,
                storeDetail = storeDetail,
                storeImages = storeImages,
                menuGroups = menuGroups,
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

data class MenuGroupResponse(
    val id: Long,
    val storeId: Long,
    val name: String,
    val priority: Int,
    val description: String?,
    val menus: List<MenuResponse>?
) {
    companion object {
        fun from(entity: MenuGroup, menus: List<MenuResponse>): MenuGroupResponse {
            return MenuGroupResponse(
                id = entity.id!!,
                storeId = entity.storeId,
                name = entity.name,
                priority = entity.priority,
                description = entity.description,
                menus = menus
            )
        }
    }
}

data class MenuResponse(
    val id: Long,
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val popularity: Boolean,
    val imageUrl: String? = null,
    val description: String? = null,
) {
    companion object {
        fun from(entity: Menu): MenuResponse {
            return MenuResponse(
                id = entity.id!!,
                menuGroupId = entity.menuGroupId,
                name = entity.name,
                price = entity.price,
                popularity = entity.popularity,
                imageUrl = entity.imageUrl,
                description = entity.description,
            )
        }
    }
}

data class StoreResponses(
    val id: Long,
    val categoryId: Long,
    val deliveryType: DeliveryType,
    val name: String,
    val deliveryFee: Long,
    val minimumOrderAmount: Long,
    val averageScore: Double,
    val menuGroups: List<MenuGroupResponses>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Store, averageScore: Double, menuGroups: List<MenuGroupResponses>): StoreResponses {
            return StoreResponses(
                id = entity.id!!,
                categoryId = entity.categoryId,
                deliveryType = entity.deliveryType,
                name = entity.name,
                deliveryFee = entity.deliveryFee,
                minimumOrderAmount = entity.minimumOrderAmount,
                averageScore = averageScore,
                menuGroups = menuGroups,
                createdAt = entity.createdAt,
            )
        }
    }
}

data class MenuGroupResponses(
    val id: Long,
    val storeId: Long,
    val menus: List<MenuResponses>
) {
    companion object {
        fun from(entity: MenuGroup, menus: List<MenuResponses>): MenuGroupResponses {
            return MenuGroupResponses(
                id = entity.id!!,
                storeId = entity.storeId,
                menus = menus
            )
        }
    }
}

data class MenuResponses(
    val id: Long,
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val imageUrl: String?,
) {
    companion object {
        fun from(entity: Menu): MenuResponses {
            return MenuResponses(
                id = entity.id!!,
                menuGroupId = entity.menuGroupId,
                name = entity.name,
                price = entity.price,
                imageUrl = entity.imageUrl,
            )
        }
    }
}
