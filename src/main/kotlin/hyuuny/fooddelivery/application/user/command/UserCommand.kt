import java.time.LocalDateTime

data class SignUpUserCommand(
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
