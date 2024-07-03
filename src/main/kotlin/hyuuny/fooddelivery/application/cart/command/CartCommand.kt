import java.time.LocalDateTime

data class CreateCartCommand(
    val userId: Long,
    val storeId: Long?,
    val deliveryFee: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class CreateCartItemCommand(
    val cartId: Long,
    val menuId: Long,
    val quantity: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class CreateCartItemOptionCommand(
    val cartItemId: Long,
    val optionId: Long,
    val createdAt: LocalDateTime,
)

data class UpdateCartItemQuantityCommand(
    val quantity: Int,
    val updatedAt: LocalDateTime,
)

data class UpdateCartUpdatedAtCommand(
    val updatedAt: LocalDateTime,
)

data class UpdateCartItemUpdatedCommand(
    val updatedAt: LocalDateTime,
)
