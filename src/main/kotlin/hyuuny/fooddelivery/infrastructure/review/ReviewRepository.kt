package hyuuny.fooddelivery.infrastructure.review

import ApiReviewSearchCondition
import hyuuny.fooddelivery.domain.review.Review
import org.springframework.data.domain.Pageable

interface ReviewRepository {

    suspend fun insert(review: Review): Review

    suspend fun findById(id: Long): Review?

    suspend fun findAllReviews(searchCondition: ApiReviewSearchCondition, pageable: Pageable): List<Review>

    suspend fun delete(id: Long)

}
