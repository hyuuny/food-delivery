package hyuuny.fooddelivery.users.infrastructure

import AdminUserSearchCondition
import hyuuny.fooddelivery.users.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepository {

    suspend fun insert(user: User): User

    suspend fun findAllUsers(searchCondition: AdminUserSearchCondition, pageable: Pageable): Page<User>

    suspend fun findById(id: Long): User?

    suspend fun findAllByIdIn(userIds: List<Long>): List<User>

    suspend fun updateName(user: User)

    suspend fun updateNickname(user: User)

    suspend fun updateEmail(user: User)

    suspend fun updatePhoneNumber(user: User)

    suspend fun updateImageUrl(user: User)

    suspend fun delete(id: Long)

    suspend fun existsByEmail(email: String): Boolean

    suspend fun existsById(id: Long): Boolean

}
