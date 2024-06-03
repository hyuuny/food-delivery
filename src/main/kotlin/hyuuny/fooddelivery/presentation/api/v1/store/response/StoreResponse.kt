package hyuuny.fooddelivery.presentation.api.v1.store.response

import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.domain.store.DeliveryType
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.store.StoreDetail
import hyuuny.fooddelivery.domain.store.StoreImage

class StoreResponse(
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
    val storeImages: List<StoreImageResponse>?,
    val menuGroups: List<MenuGroupResponse>?,
) {
    companion object {
        fun from(
            entity: Store,
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
