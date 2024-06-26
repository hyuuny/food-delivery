package hyuuny.fooddelivery.infrastructure.user

import AdminUserSearchCondition
import hyuuny.fooddelivery.domain.user.User
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import selectAndCount

@Component
class UserRepositoryImpl(
    private val dao: UserDao,
    private val template: R2dbcEntityTemplate,
) : UserRepository {

    override suspend fun insert(user: User): User = dao.save(user)

    override suspend fun findAllUsers(searchCondition: AdminUserSearchCondition, pageable: Pageable): Page<User> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<User>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun findById(id: Long): User? = dao.findById(id)

    override suspend fun findAllByIdIn(userIds: List<Long>): List<User> = dao.findAllById(userIds).toList()

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
        template.update<User>()
            .matching(
                Query.query(
                    where("id").`is`(user.id!!)
                ),
            ).applyAndAwait(
                Update.update("phoneNumber", user.phoneNumber)
                    .set("updatedAt", user.updatedAt)
            )
    }

    override suspend fun updateImageUrl(user: User) {
        template.update<User>()
            .matching(
                Query.query(
                    where("id").`is`(user.id!!)
                ),
            ).applyAndAwait(
                Update.update("imageUrl", user.imageUrl)
                    .set("updatedAt", user.updatedAt)
            )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun existsByEmail(email: String): Boolean = dao.existsByEmail(email)

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    private fun buildCriteria(searchCondition: AdminUserSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.name?.let {
            criteria = criteria.and("name").like("$it%")
        }

        searchCondition.nickname?.let {
            criteria = criteria.and("nickname").like("%$it%")
        }

        searchCondition.email?.let {
            criteria = criteria.and("email").like("%$it%")
        }

        searchCondition.phoneNumber?.let {
            criteria = criteria.and("phoneNumber").`is`(it)
        }

        searchCondition.fromDate?.let {
            criteria = criteria.and("createdAt").greaterThanOrEquals(it)
        }

        searchCondition.toDate?.let {
            criteria = criteria.and("createdAt").lessThanOrEquals(it)
        }

        return criteria
    }

}
