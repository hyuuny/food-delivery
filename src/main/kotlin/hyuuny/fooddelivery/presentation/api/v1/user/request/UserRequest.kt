data class SignUpUserRequest(
    val name: String,
    val nickname: String,
    val email: String,
    val phoneNumber: String,
    val imageUrl: String?,
)

data class ChangeUserNameRequest(
    val name: String,
)

data class ChangeUserNicknameRequest(
    val nickname: String
)

data class ChangeUserEmailRequest(
    val email: String
)

data class ChangeUserPhoneNumberRequest(
    val phoneNumber: String
)

data class ChangeUserImageUrlRequest(
    val imageUrl: String?
)
