package hyuuny.fooddelivery.reviews.application

import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.reviews.infrastructure.ReviewPhotoRepository
import hyuuny.fooddelivery.reviews.infrastructure.ReviewRepository
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

class DeleteReviewUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewRepository>()
    val reviewPhotoRepository = mockk<ReviewPhotoRepository>()
    val userUseCase = mockk<UserUseCase>()
    val useCase = ReviewUseCase(repository, reviewPhotoRepository)

    Given("회원이 리뷰를 삭제할 때") {
        val userId = 1L
        val storeId = 1L
        val orderId = 1L
        val reviewId = 1L

        val now = LocalDateTime.now()
        val user = User(
            id = userId,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now.minusMonths(5),
            updatedAt = now.minusMonths(5),
        )

        val review = Review(
            id = reviewId,
            userId = userId,
            storeId = storeId,
            orderId = orderId,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now,
        )
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { repository.findById(any()) } returns review
        coEvery { repository.delete(any()) } returns Unit
        coEvery { reviewPhotoRepository.deleteAllByReviewId(any()) } returns Unit

        `when`("삭제할 수 있는 리뷰이면") {
            useCase.deleteReview(reviewId) { userUseCase.getUser(userId) }

            then("리뷰가 정상적으로 삭제된다") {
                coVerify { repository.delete(any()) }
            }
        }

        `when`("회원과 리뷰의 회원이 서로 다르면") {
            coEvery { repository.findById(any()) } returns Review(
                id = reviewId,
                userId = 920L,
                storeId = storeId,
                orderId = orderId,
                score = 5,
                content = "맛있어요. 다음에 또 주문할게요.",
                createdAt = now,
            )
            then("리뷰 삭제 권한이 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.deleteReview(reviewId) { userUseCase.getUser(userId) }
                }
                ex.message shouldBe "리뷰 삭제 권한이 없습니다."
            }
        }

        `when`("존재하지 않는 회원이면") {
            coEvery { userUseCase.getUser(any()) } throws NoSuchElementException("0번 회원을 찾을 수 없습니다.")

            then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteReview(reviewId) { userUseCase.getUser(0) }
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 리뷰이면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { repository.findById(any()) } throws NoSuchElementException("0번 리뷰를 찾을 수 없습니다.")

            then("리뷰를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteReview(reviewId) { userUseCase.getUser(userId) }
                }
                ex.message shouldBe "0번 리뷰를 찾을 수 없습니다."
            }
        }
    }

})
