package hyuuny.fooddelivery.infrastructure.reviewcomment

import AdminReviewCommentSearchCondition
import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import selectAndCount

@Component
class ReviewCommentRepositoryImpl(
    private val dao: ReviewCommentDao,
    private val template: R2dbcEntityTemplate,
) : ReviewCommentRepository {

    override suspend fun insert(reviewComment: ReviewComment): ReviewComment = dao.save(reviewComment)

    override suspend fun findById(id: Long): ReviewComment? = dao.findById(id)

    override suspend fun findAllReviewComments(
        searchCondition: AdminReviewCommentSearchCondition,
        pageable: Pageable
    ): PageImpl<ReviewComment> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<ReviewComment>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun updateContent(reviewComment: ReviewComment) {
        template.update<ReviewComment>()
            .matching(
                Query.query(
                    Criteria.where("id").`is`(reviewComment.id!!)
                ),
            ).applyAndAwait(
                Update.update("content", reviewComment.content)
                    .set("updatedAt", reviewComment.updatedAt)
            )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    private fun buildCriteria(searchCondition: AdminReviewCommentSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.userId?.let {
            criteria = criteria.and("user_id").`is`(it)
        }

        searchCondition.reviewId?.let {
            criteria = criteria.and("review_id").`is`(it)
        }

        searchCondition.fromDate?.let {
            criteria = criteria.and("created_at").greaterThanOrEquals(it)
        }

        searchCondition.toDate?.let {
            criteria = criteria.and("created_at").lessThanOrEquals(it)
        }

        return criteria
    }
}
