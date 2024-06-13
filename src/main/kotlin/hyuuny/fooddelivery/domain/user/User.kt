package hyuuny.fooddelivery.domain.user

import ChangeEmailCommand
import ChangeUserNameCommand
import ChangeUserNicknameCommand
import SignUpUserCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
class User(
    id: Long? = null,
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

    fun handle(command: ChangeEmailCommand) {
        this.email = command.email
        this.updatedAt = command.updatedAt
    }

}