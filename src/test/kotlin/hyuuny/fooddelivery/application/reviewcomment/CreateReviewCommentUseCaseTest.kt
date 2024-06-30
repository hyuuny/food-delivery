package hyuuny.fooddelivery.application.reviewcomment

import CreateReviewCommentRequest
import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.reviewcomment.ReviewCommentRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateReviewCommentUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewCommentRepository>()
    val useCase = ReviewCommentUseCase(repository)
    val userUseCase = mockk<UserUseCase>()
    val reviewUseCase = mockk<ReviewUseCase>()

    Given("사장님이 리뷰에 댓글을 작성할 때") {
        val userId = 1L
        val reviewId = 1L

        val now = LocalDateTime.now()
        val user = User(
            id = userId,
            name = "사장님",
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
            storeId = 1L,
            orderId = 1L,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now.minusDays(1),
        )

        val request = CreateReviewCommentRequest(
            reviewId = reviewId,
            userId = userId,
            content = "감사합니다. 다음에 또 방문해주세요.\uD83D\uDE00\uD83D\uDE03",
        )

        val reviewComment = ReviewComment(
            id = 1L,
            reviewId = reviewId,
            userId = userId,
            content = "감사합니다. 다음에 또 방문해주세요.\uD83D\uDE00\uD83D\uDE03",
            createdAt = now,
            updatedAt = now,
        )

        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { reviewUseCase.getReview(any()) } returns review
        coEvery { repository.insert(any()) } returns reviewComment

        `when`("유효한 리뷰에 댓글을 작성하면") {
            val result = useCase.createReviewComment(
                request = request,
                getOwner = { userUseCase.getUser(userId) },
                getReview = { reviewUseCase.getReview(reviewId) },
            )

            then("댓글이 정상적으로 생성된다") {
                result.id.shouldNotBeNull()
                result.userId shouldBe request.userId
                result.reviewId shouldBe request.reviewId
                result.content shouldBe request.content
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("존재하지 않는 회원이면") {
            coEvery { userUseCase.getUser(any()) } throws NoSuchElementException("0번 회원을 찾을 수 없습니다.")

            then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createReviewComment(
                        request = request,
                        getOwner = { userUseCase.getUser(0) },
                        getReview = { reviewUseCase.getReview(reviewId) },
                    )
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 리뷰이면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { reviewUseCase.getReview(any()) } throws NoSuchElementException("0번 리뷰를 찾을 수 없습니다.")

            then("리뷰를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createReviewComment(
                        request = request,
                        getOwner = { userUseCase.getUser(userId) },
                        getReview = { reviewUseCase.getReview(0) },
                    )
                }
                ex.message shouldBe "0번 리뷰를 찾을 수 없습니다."
            }
        }
    }

})
