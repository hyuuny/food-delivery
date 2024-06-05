import hyuuny.fooddelivery.common.constant.DeliveryType

data class CreateCategoryRequest(
    val deliveryType: DeliveryType,
    val name: String,
    val priority: Int,
    val iconImageUrl: String,
    val visible: Boolean,
)

data class AdminCategorySearchCondition(
    val id: Long?,
    val deliveryType: DeliveryType?,
    val name: String?,
    val visible: Boolean?,
)