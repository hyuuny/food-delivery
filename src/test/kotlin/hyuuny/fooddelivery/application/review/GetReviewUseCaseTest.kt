package hyuuny.fooddelivery.application.review

import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.infrastructure.review.ReviewPhotoRepository
import hyuuny.fooddelivery.infrastructure.review.ReviewRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetReviewUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewRepository>()
    val reviewPhotoRepository = mockk<ReviewPhotoRepository>()
    val useCase = ReviewUseCase(repository, reviewPhotoRepository)

    Given("리뷰를 상세조회 할 때") {
        val reviewId = 1L

        val now = LocalDateTime.now()
        val review = Review(
            id = reviewId,
            userId = 3L,
            storeId = 123L,
            orderId = 2392L,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now,
        )
        coEvery { repository.findById(any()) } returns review

        `when`("존재하는 리뷰라면") {
            val result = useCase.getReview(reviewId)

            then("리뷰를 상세조회 할 수 있다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe review.userId
                result.storeId shouldBe review.storeId
                result.orderId shouldBe review.orderId
                result.score shouldBe review.score
                result.content shouldBe review.content
                result.createdAt.shouldNotBeNull()
            }
        }

        `when`("존재하지 않는 리뷰라면") {
            coEvery { repository.findById(any()) } throws NoSuchElementException("0번 리뷰를 찾을 수 없습니다.")

            then("리뷰를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getReview(0)
                }
                ex.message shouldBe "0번 리뷰를 찾을 수 없습니다."
            }
        }
    }

})
