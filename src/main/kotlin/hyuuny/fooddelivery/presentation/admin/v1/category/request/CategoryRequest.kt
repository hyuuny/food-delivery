import hyuuny.fooddelivery.common.constant.DeliveryType

data class CreateCategoryRequest(
    val deliveryType: DeliveryType,
    val name: String,
    val priority: Int,
    val iconImageUrl: String,
    val visible: Boolean,
)

data class UpdateCategoryRequest(
    val deliveryType: DeliveryType,
    val name: String,
    val iconImageUrl: String,
    val visible: Boolean,
)

data class ReOrderCategoryRequests(
    val reOrderedCategories: List<ReOrderCategoryRequest>,
)

data class ReOrderCategoryRequest(
    val categoryId: Long,
    val priority: Int,
)


data class AdminCategorySearchCondition(
    val id: Long?,
    val deliveryType: DeliveryType?,
    val name: String?,
    val visible: Boolean?,
)