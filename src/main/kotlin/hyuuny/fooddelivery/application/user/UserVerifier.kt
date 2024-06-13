package hyuuny.fooddelivery.application.user

import SignUpUserRequest

object UserVerifier {

    private const val EMAIL_PATTERN = ("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    private const val PHONE_NUMBER_PATTERN = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}\$"

    fun verify(request: SignUpUserRequest) {
        verifyName(request.name)
        verifyNickname(request.nickname)
        verifyEmail(request.email)
        verifyPhoneNumber(request.phoneNumber)
    }

    fun verifyName(name: String) {
        if (name.length < 2) throw IllegalArgumentException("이름은 최소 2자 이상이여야 합니다. name: $name")
    }

    fun verifyNickname(nickname: String) {
        if (nickname.length !in 2..10) throw IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다. nickname: $nickname")
    }

    fun verifyEmail(email: String) {
        if (!email.matches(Regex(EMAIL_PATTERN))) throw IllegalArgumentException("올바른 이메일 형식이 아닙니다. email: $email")
    }

    fun verifyPhoneNumber(phoneNumber: String) {
        if (!phoneNumber.matches(Regex(PHONE_NUMBER_PATTERN))) throw IllegalArgumentException("올바른 휴대폰 번호 형식이 아닙니다. phoneNumber: $phoneNumber")
    }

}
