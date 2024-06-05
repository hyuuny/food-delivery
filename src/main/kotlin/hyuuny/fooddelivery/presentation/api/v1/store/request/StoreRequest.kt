import hyuuny.fooddelivery.domain.store.DeliveryType

data class ApiStoreSearchCondition(
    val categoryId: Long?,
    val deliveryType: DeliveryType?,
    val name: String?
)