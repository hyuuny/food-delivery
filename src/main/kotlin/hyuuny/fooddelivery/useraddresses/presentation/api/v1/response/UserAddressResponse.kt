package hyuuny.fooddelivery.useraddresses.presentation.api.v1.response

import hyuuny.fooddelivery.useraddresses.domain.UserAddress
import java.time.LocalDateTime

data class UserAddressResponse(
    val id: Long,
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
) {
    companion object {
        fun from(entity: UserAddress): UserAddressResponse {
            return UserAddressResponse(
                id = entity.id!!,
                userId = entity.userId,
                name = entity.name ?: entity.address,
                zipCode = entity.zipCode,
                address = entity.address,
                detailAddress = entity.detailAddress,
                messageToRider = entity.messageToRider,
                entrancePassword = entity.entrancePassword,
                routeGuidance = entity.routeGuidance,
                selected = entity.selected,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
        }
    }
}
