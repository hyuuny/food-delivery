package hyuuny.fooddelivery.presentation.api.v1.user.response

import hyuuny.fooddelivery.domain.user.User
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
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
                name = entity.name,
                nickname = entity.nickname,
                email = entity.email,
                phoneNumber = entity.phoneNumber,
                imageUrl = entity.imageUrl,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
        }
    }
}
