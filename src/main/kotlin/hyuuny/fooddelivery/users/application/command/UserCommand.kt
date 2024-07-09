import hyuuny.fooddelivery.common.constant.UserType
import java.time.LocalDateTime

data class SignUpUserCommand(
    val userType: UserType,
    val name: String,
    val nickname: String,
    val email: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class ChangeUserNameCommand(
    val name: String,
    val updatedAt: LocalDateTime,
)

data class ChangeUserNicknameCommand(
    val nickname: String,
    val updatedAt: LocalDateTime,
)

data class ChangeUserEmailCommand(
    val email: String,
    val updatedAt: LocalDateTime,
)

data class ChangeUserPhoneNumberCommand(
    val phoneNumber: String,
    val updatedAt: LocalDateTime,
)

data class ChangeUserImageUrlCommand(
    val imageUrl: String?,
    val updatedAt: LocalDateTime,
)
