package hyuuny.fooddelivery.application.review

import AdminReviewSearchCondition
import ApiReviewSearchCondition
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class ReviewUseCase(
    private val repository: ReviewRepository,
    private val reviewPhotoRepository: ReviewPhotoRepository,
) {

    suspend fun getReviewByAdminCondition(
        searchCondition: AdminReviewSearchCondition,
        pageable: Pageable
    ): PageImpl<Review> {
        val page = repository.findAllReviews(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun getReviewByApiCondition(
        searchCondition: ApiReviewSearchCondition,
        pageable: Pageable
    ): PageImpl<Review> {
        val page = repository.findAllReviews(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun getAllByUserIds(userIds: List<Long>): List<Review> =
        repository.findAllByUserIdIn(userIds)

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

        if (order.storeId != request.storeId) throw IllegalArgumentException("매장과 주문한 매장이 서로 다릅니다.")
        if (repository.existsByUserIdAndOrderId(user.id!!, order.id!!)) throw IllegalArgumentException("이미 등록된 리뷰입니다.")

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

    @Transactional
    suspend fun deleteReview(
        id: Long,
        getUser: suspend () -> User,
    ) {
        val user = getUser()
        val review = findReviewByIdOrThrow(id)
        if (review.userId != user.id) throw IllegalArgumentException("리뷰 삭제 권한이 없습니다.")
        repository.delete(id)
        reviewPhotoRepository.deleteAllByReviewId(id)
    }

    suspend fun getAverageScoreByStoreIds(storeIds: List<Long>): Map<Long, Double> =
        repository.findAverageScoreByStoreId(storeIds)

    private suspend fun findReviewByIdOrThrow(id: Long) =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 리뷰를 찾을 수 없습니다.")

}
