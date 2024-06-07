import hyuuny.fooddelivery.common.constant.DeliveryType
import java.time.LocalDateTime

data class CreateCategoryCommand(
    val deliveryType: DeliveryType,
    val name: String,
    val priority: Int,
    val iconImageUrl: String,
    val visible: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class UpdateCategoryCommand(
    val deliveryType: DeliveryType,
    val name: String,
    val iconImageUrl: String,
    val visible: Boolean,
    val updatedAt: LocalDateTime,
)