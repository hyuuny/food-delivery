package hyuuny.fooddelivery.infrastructure.review

import AdminReviewSearchCondition
import ApiReviewSearchCondition
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.infrastructure.store.StoreDao
import hyuuny.fooddelivery.infrastructure.user.UserDao
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import selectAndCount
import java.math.BigDecimal

@Component
class ReviewRepositoryImpl(
    private val dao: ReviewDao,
    private val userDao: UserDao,
    private val storeDao: StoreDao,
    private val template: R2dbcEntityTemplate,
) : ReviewRepository {

    override suspend fun insert(review: Review): Review = dao.save(review)

    override suspend fun findById(id: Long): Review? = dao.findById(id)

    override suspend fun findAllReviews(
        searchCondition: AdminReviewSearchCondition,
        pageable: Pageable
    ): PageImpl<Review> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Review>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun findAllReviews(
        searchCondition: ApiReviewSearchCondition,
        pageable: Pageable
    ): PageImpl<Review> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Review>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun findAllByUserIdIn(userIds: List<Long>): List<Review> {
        if (userIds.isEmpty()) return emptyList()
        return dao.findAllByUserIdIn(userIds)
    }

    override suspend fun existsByUserIdAndOrderId(userId: Long, orderId: Long): Boolean =
        dao.existsByUserIdAndOrderId(userId, orderId)

    override suspend fun findAverageScoreByStoreId(storeIds: List<Long>): Map<Long, Double> {
        return template.databaseClient
            .sql("SELECT store_id AS scoreId, COALESCE(AVG(score), 0.0) AS averageScore FROM reviews WHERE store_id in (:storeId) GROUP BY store_id")
            .bind("storeId", storeIds)
            .map { row ->
                val storeId = row.get("scoreId", BigDecimal::class.java)!!.toLong()
                val averageScore = row.get("averageScore", BigDecimal::class.java)?.toDouble() ?: 0.0
                storeId to averageScore
            }
            .all()
            .collectList()
            .awaitSingle()
            .toMap()
    }

    private suspend fun buildCriteria(searchCondition: AdminReviewSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.userId?.let {
            criteria = criteria.and("userId").`is`(it)
        }

        searchCondition.userName?.let {
            val userIds = userDao.findAllByName(it).mapNotNull { it.id }
            criteria = criteria.and("userId").`in`(userIds)
        }

        searchCondition.userNickname?.let {
            val userIds = userDao.findAllByNickname(it).mapNotNull { it.id }
            criteria = criteria.and("userId").`in`(userIds)
        }

        searchCondition.storeId?.let {
            criteria = criteria.and("storeId").`is`(it)
        }

        searchCondition.storeName?.let {
            val storeIds = storeDao.findAllByNameContaining(it).mapNotNull { it.id }
            criteria = criteria.and("storeId").`in`(storeIds)
        }

        searchCondition.orderId?.let {
            criteria = criteria.and("orderId").`is`(it)
        }

        searchCondition.fromDate?.let {
            criteria = criteria.and("createdAt").greaterThanOrEquals(it)
        }

        searchCondition.toDate?.let {
            criteria = criteria.and("createdAt").lessThanOrEquals(it)
        }

        return criteria
    }

    private fun buildCriteria(searchCondition: ApiReviewSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.userId?.let {
            criteria = criteria.and("userId").`is`(it)
        }

        searchCondition.storeId?.let {
            criteria = criteria.and("storeId").`is`(it)
        }

        return criteria
    }

}
