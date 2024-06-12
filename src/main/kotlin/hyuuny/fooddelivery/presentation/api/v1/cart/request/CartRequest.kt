data class AddItemToCartRequest(
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