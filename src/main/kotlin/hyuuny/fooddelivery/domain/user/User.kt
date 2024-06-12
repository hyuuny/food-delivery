package hyuuny.fooddelivery.domain.user

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
}