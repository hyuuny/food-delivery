package hyuuny.fooddelivery.application.review

import CreateReviewCommand
import CreateReviewPhotoCommand
import CreateReviewRequest
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.review.ReviewPhoto
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.review.ReviewPhotoRepository
import hyuuny.fooddelivery.infrastructure.review.ReviewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class ReviewUseCase(
    private val repository: ReviewRepository,
    private val reviewPhotoRepository: ReviewPhotoRepository,
) {

    @Transactional
    suspend fun createReview(
        request: CreateReviewRequest,
        getUser: suspend () -> User,
        getStore: suspend (storeId: Long) -> Store,
        getOrder: suspend (orderId: Long) -> Order,
    ): Review {
        if (!(1..5).contains(request.score)) throw IllegalArgumentException("잘못된 리뷰 평점입니다.")

        val now = LocalDateTime.now()
        val user = getUser()
        val store = getStore(request.storeId)
        val order = getOrder(request.orderId)
        val review = Review.handle(
            CreateReviewCommand(
                userId = user.id!!,
                storeId = store.id!!,
                orderId = order.id!!,
                score = request.score,
                content = request.content,
                createdAt = now,
            )
        )
        val savedReview = repository.insert(review)

        request.photos.map {
            ReviewPhoto.handle(
                CreateReviewPhotoCommand(
                    reviewId = savedReview.id!!,
                    photoUrl = it.photoUrl,
                    createdAt = now,
                )
            )
        }.also { reviewPhotoRepository.insertAll(it) }

        return savedReview
    }

}
