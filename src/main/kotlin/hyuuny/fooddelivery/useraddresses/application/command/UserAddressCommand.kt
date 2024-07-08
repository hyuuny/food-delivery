import java.time.LocalDateTime

data class CreateUserAddressCommand(
    val userId: Long,
    val name: String,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val messageToRider: String?,
    val entrancePassword: String?,
    val routeGuidance: String?,
    val selected: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class ChangeUserAddressSelectedCommand(
    val selected: Boolean,
    val updatedAt: LocalDateTime,
)

data class UpdateUserAddressCommand(
    val name: String,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val messageToRider: String?,
    val entrancePassword: String?,
    val routeGuidance: String?,
    val updatedAt: LocalDateTime,
)
