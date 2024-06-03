import hyuuny.fooddelivery.domain.store.DeliveryType

data class StoreApiSearchCondition(
    val categoryId: Long?,
    val deliveryType: DeliveryType?,
    val name: String?
)