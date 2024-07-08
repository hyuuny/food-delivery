import hyuuny.fooddelivery.common.constant.DeliveryType

data class ApiStoreSearchCondition(
    val categoryId: Long?,
    val deliveryType: DeliveryType?,
    val name: String?
)