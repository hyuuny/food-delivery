import hyuuny.fooddelivery.domain.store.DeliveryType

data class CreateStoreRequest(
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
    val storeDetail: CreateStoreDetailRequest,
    val storeImage: CreateStoreImageRequest,
)

data class CreateStoreDetailRequest(
    val zipCode: String,
    val address: String,
    val detailedAddress: String?,
    val openHours: String?,
    val closedDay: String?
)

data class CreateStoreImageRequest(
    val imageUrls: List<String>,
)

data class StoreSearchCondition(
    val id: Long? = null,
    val categoryId: Long? = null,
    val deliveryType: DeliveryType? = null,
    val name: String? = null,
    val taxId: String? = null,
    val phoneNumber: String? = null,
)