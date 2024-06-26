package hyuuny.fooddelivery.application.review

import hyuuny.fooddelivery.infrastructure.review.ReviewPhotoRepository
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
