package hyuuny.fooddelivery.reviews.infrastructure

import AdminReviewSearchCondition
import ApiReviewSearchCondition
import hyuuny.fooddelivery.reviews.domain.Review
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface ReviewRepository {

    suspend fun insert(review: Review): Review

    suspend fun findById(id: Long): Review?

    suspend fun findAllReviews(searchCondition: AdminReviewSearchCondition, pageable: Pageable): PageImpl<Review>

    suspend fun findAllReviews(searchCondition: ApiReviewSearchCondition, pageable: Pageable): PageImpl<Review>

    suspend fun delete(id: Long)

    suspend fun findAllByUserIdIn(userIds: List<Long>): List<Review>

    suspend fun existsByUserIdAndOrderId(userId: Long, orderId: Long): Boolean

    suspend fun findAverageScoreByStoreId(storeIds: List<Long>): Map<Long, Double>

}
