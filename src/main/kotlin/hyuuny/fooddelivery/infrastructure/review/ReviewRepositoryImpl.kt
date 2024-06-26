package hyuuny.fooddelivery.infrastructure.review

import ApiReviewSearchCondition
import hyuuny.fooddelivery.domain.review.Review
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class ReviewRepositoryImpl(
    private val dao: ReviewDao,
    private val template: R2dbcEntityTemplate,
) : ReviewRepository {

    override suspend fun insert(review: Review): Review = dao.save(review)

    override suspend fun findById(id: Long): Review? {
        TODO("Not yet implemented")
    }

    override suspend fun findAllReviews(searchCondition: ApiReviewSearchCondition, pageable: Pageable): List<Review> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

}
