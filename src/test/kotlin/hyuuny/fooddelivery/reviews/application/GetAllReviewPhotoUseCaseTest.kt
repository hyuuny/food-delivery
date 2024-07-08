package hyuuny.fooddelivery.reviews.application

import hyuuny.fooddelivery.reviews.domain.ReviewPhoto
import hyuuny.fooddelivery.reviews.infrastructure.ReviewPhotoRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetAllReviewPhotoUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewPhotoRepository>()
    val useCase = ReviewPhotoUseCase(repository)

    Given("리뷰에 포함된") {
        val reviewId = 1L

        val now = LocalDateTime.now()
        val reviewPhotos = listOf(
            ReviewPhoto(1L, reviewId, "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-1.jpg", now),
            ReviewPhoto(2L, reviewId, "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-2.jpg", now),
            ReviewPhoto(3L, reviewId, "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-3.jpg", now),
        )
        coEvery { repository.findAllByReviewId(any()) } returns reviewPhotos

        `when`("포토 목록을") {
            val result = useCase.getAllByReviewId(reviewId)

            then("조회한다.") {
                result.forEachIndexed { index, photo ->
                    photo.id shouldBe reviewPhotos[index].id
                    photo.reviewId shouldBe reviewPhotos[index].reviewId
                    photo.photoUrl shouldBe reviewPhotos[index].photoUrl
                    photo.createdAt.toString() shouldBe reviewPhotos[index].createdAt.toString()
                }
            }
        }

        `when`("포토가 없으면") {
            coEvery { repository.findAllByReviewId(any()) } returns emptyList()
            val result = useCase.getAllByReviewId(reviewId)

            then("빈 목록이 조회된다.") {
                result.size shouldBe 0
            }
        }
    }

})
