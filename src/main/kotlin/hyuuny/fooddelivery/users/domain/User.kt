package hyuuny.fooddelivery.users.domain

import ChangeUserEmailCommand
import ChangeUserImageUrlCommand
import ChangeUserNameCommand
import ChangeUserNicknameCommand
import ChangeUserPhoneNumberCommand
import SignUpUserCommand
import hyuuny.fooddelivery.common.constant.UserType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
class User(
    id: Long? = null,
    val userType: UserType = UserType.CUSTOMER,
    name: String,
    nickname: String,
    email: String,
    phoneNumber: String,
    imageUrl: String? = null,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        private set
    var name = name
        private set
    var nickname = nickname
        private set
    var email = email
        private set
    var phoneNumber = phoneNumber
        private set
    var imageUrl = imageUrl
        private set
    var updatedAt = updatedAt
        private set

    companion object {

        const val USER_DEFAULT_IMAGE_URL =
            "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/user-default.jpeg"

        fun handle(command: SignUpUserCommand): User = User(
            name = command.name,
            nickname = command.nickname,
            email = command.email,
            phoneNumber = command.phoneNumber,
            imageUrl = command.imageUrl,
            createdAt = command.createdAt,
            updatedAt = command.updatedAt,
        )
    }

    fun handle(command: ChangeUserNameCommand) {
        this.name = command.name
        this.updatedAt = command.updatedAt
    }

    fun handle(command: ChangeUserNicknameCommand) {
        this.nickname = command.nickname
        this.updatedAt = command.updatedAt
    }

    fun handle(command: ChangeUserEmailCommand) {
        this.email = command.email
        this.updatedAt = command.updatedAt
    }

    fun handle(command: ChangeUserPhoneNumberCommand) {
        this.phoneNumber = command.phoneNumber
        this.updatedAt = command.updatedAt
    }

    fun handle(command: ChangeUserImageUrlCommand) {
        this.imageUrl = command.imageUrl
        this.updatedAt = command.updatedAt
    }

    fun getImageUrlOrDefault(): String = imageUrl ?: USER_DEFAULT_IMAGE_URL

    fun isCustomer(): Boolean = userType == UserType.CUSTOMER

    fun isRider(): Boolean = userType == UserType.RIDER

}
