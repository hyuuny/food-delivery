package hyuuny.fooddelivery.useraddresses.presentation.api.v1.request

data class CreateUserAddressRequest(
    val name: String,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val messageToRider: String?,
    val entrancePassword: String?,
    val routeGuidance: String?,
)

data class UpdateUserAddressRequest(
    val name: String,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val messageToRider: String?,
    val entrancePassword: String?,
    val routeGuidance: String?,
)
