package hyuuny.fooddelivery.carts.presentation.api.v1.request

data class AddItemToCartRequest(
    val storeId: Long,
    val item: AddItemAndOptionRequest,
)

data class AddItemAndOptionRequest(
    val menuId: Long,
    val quantity: Int,
    val optionIds: List<Long>,
)

data class UpdateCartItemQuantityRequest(
    val quantity: Int,
)

data class UpdateCartItemOptionsRequest(
    val optionIds: List<Long>,
)
