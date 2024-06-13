package hyuuny.fooddelivery.infrastructure.user

import hyuuny.fooddelivery.domain.user.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserDao : CoroutineCrudRepository<User, Long> {

    suspend fun existsByEmail(email: String): Boolean

}
