package hyuuny.fooddelivery.infrastructure.user

import AdminUserSearchCondition
import hyuuny.fooddelivery.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val dao: UserDao,
    private val template: R2dbcEntityTemplate,
) : UserRepository {

    override suspend fun insert(user: User): User = dao.save(user)

    override suspend fun findAllUsers(searchCondition: AdminUserSearchCondition, pageable: Pageable): Page<User> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): User? = dao.findById(id)

    override suspend fun updateName(user: User) {
        template.update<User>()
            .matching(
                Query.query(
                    where("id").`is`(user.id!!)
                ),
            ).applyAndAwait(
                Update.update("name", user.name)
                    .set("updatedAt", user.updatedAt)
            )
    }

    override suspend fun updateNickname(user: User) {
        template.update<User>()
            .matching(
                Query.query(
                    where("id").`is`(user.id!!)
                ),
            ).applyAndAwait(
                Update.update("nickname", user.nickname)
                    .set("updatedAt", user.updatedAt)
            )
    }

    override suspend fun updateEmail(user: User) {
        template.update<User>()
            .matching(
                Query.query(
                    where("id").`is`(user.id!!)
                ),
            ).applyAndAwait(
                Update.update("email", user.email)
                    .set("updatedAt", user.updatedAt)
            )
    }

    override suspend fun updatePhoneNumber(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun updateImageUrl(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun existsByEmail(email: String): Boolean = dao.existsByEmail(email)

}
