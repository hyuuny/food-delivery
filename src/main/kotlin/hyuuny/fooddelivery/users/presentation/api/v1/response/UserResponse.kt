package hyuuny.fooddelivery.users.presentation.api.v1.response

import hyuuny.fooddelivery.common.constant.UserType
import hyuuny.fooddelivery.users.domain.User
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val userType: UserType,
    val name: String,
    val nickname: String,
    val email: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: User): UserResponse {
            return UserResponse(
                id = entity.id!!,
                userType = entity.userType,
                name = entity.name,
                nickname = entity.nickname,
                email = entity.email,
                phoneNumber = entity.phoneNumber,
                imageUrl = entity.getImageUrlOrDefault(),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
        }
    }
}
