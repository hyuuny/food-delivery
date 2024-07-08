package hyuuny.fooddelivery.users.infrastructure

import hyuuny.fooddelivery.users.domain.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserDao : CoroutineCrudRepository<User, Long> {

    suspend fun existsByEmail(email: String): Boolean

    suspend fun findAllByName(name: String): List<User>

    suspend fun findAllByNickname(nickname: String): List<User>

}
