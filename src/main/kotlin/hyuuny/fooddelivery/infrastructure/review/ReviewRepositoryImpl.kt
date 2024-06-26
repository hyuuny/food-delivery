package hyuuny.fooddelivery.infrastructure.review

import ApiReviewSearchCondition
import hyuuny.fooddelivery.domain.review.Review
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import selectAndCount

@Component
class ReviewRepositoryImpl(
    private val dao: ReviewDao,
    private val template: R2dbcEntityTemplate,
) : ReviewRepository {

    override suspend fun insert(review: Review): Review = dao.save(review)

    override suspend fun findById(id: Long): Review? {
        TODO("Not yet implemented")
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

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByUserIdIn(userIds: List<Long>): List<Review> {
        if (userIds.isEmpty()) return emptyList()
        return dao.findAllByUserIdIn(userIds)
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
