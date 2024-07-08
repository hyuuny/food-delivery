package hyuuny.fooddelivery.reviews.application

import hyuuny.fooddelivery.reviews.infrastructure.ReviewPhotoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ReviewPhotoUseCase(
    private val repository: ReviewPhotoRepository,
) {

    suspend fun getAllByReviewIdIn(reviewIds: List<Long>) = repository.findAllByReviewIdIn(reviewIds)

    suspend fun getAllByReviewId(reviewId: Long) = repository.findAllByReviewId(reviewId)

}
