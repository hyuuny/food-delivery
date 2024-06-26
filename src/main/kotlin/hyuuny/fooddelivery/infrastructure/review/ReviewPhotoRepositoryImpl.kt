package hyuuny.fooddelivery.infrastructure.review

import hyuuny.fooddelivery.domain.review.ReviewPhoto
import kotlinx.coroutines.flow.toList
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class ReviewPhotoRepositoryImpl(
    private val dao: ReviewPhotoDao,
    private val template: R2dbcEntityTemplate,
) : ReviewPhotoRepository {

    override suspend fun insertAll(reviewPhotos: List<ReviewPhoto>): List<ReviewPhoto> =
        dao.saveAll(reviewPhotos).toList()

    override suspend fun findAllByReviewIdIn(reviewIds: List<Long>): List<ReviewPhoto> =
        dao.findAllByReviewIdIn(reviewIds)

    override suspend fun findAllByReviewId(reviewId: Long): List<ReviewPhoto> = dao.findAllByReviewId(reviewId)

    override suspend fun deleteAllByReviewId(reviewId: Long) {
        TODO("Not yet implemented")
    }

}
