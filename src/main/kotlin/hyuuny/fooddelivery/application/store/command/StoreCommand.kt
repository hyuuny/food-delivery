import hyuuny.fooddelivery.domain.store.DeliveryType
import java.time.LocalDateTime

data class CreateStoreCommand(
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
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class CreateStoreDetailCommand(
    val storeId: Long,
    val zipCode: String,
    val address: String,
    val detailedAddress: String?,
    val openHours: String?,
    val closedDay: String?,
    val createdAt: LocalDateTime,
)

data class CreateStoreImageCommand(
    val storeId: Long,
    val imageUrl: String,
    val createdAt: LocalDateTime,
)