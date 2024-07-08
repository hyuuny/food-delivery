package hyuuny.fooddelivery.reviewcomments.application

import hyuuny.fooddelivery.reviewcomments.domain.ReviewComment
import hyuuny.fooddelivery.reviewcomments.infrastructure.ReviewCommentRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetReviewCommentUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewCommentRepository>()
    val useCase = ReviewCommentUseCase(repository)

    Given("리뷰 댓글을 상세조회 할 때") {
        val id = 1L

        val now = LocalDateTime.now()
        val reviewComment = ReviewComment(
            id = id,
            reviewId = 1L,
            userId = 1L,
            content = "감사합니다. 다음에 또 방문해주세요.\uD83D\uDE00\uD83D\uDE03",
            createdAt = now,
            updatedAt = now,
        )

        coEvery { repository.findById(any()) } returns reviewComment

        `when`("존재하는 리뷰 댓글이라면") {
            val result = useCase.getReviewComment(id)

            then("상세조회 할 수 있다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe reviewComment.userId
                result.reviewId shouldBe reviewComment.reviewId
                result.content shouldBe reviewComment.content
                result.createdAt shouldBe reviewComment.createdAt
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("존재하지 않는 리뷰 댓글이라면") {
            coEvery { repository.findById(any()) } returns null

            then("리뷰 댓글을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getReviewComment(0)
                }
                ex.message shouldBe "0번 리뷰 댓글을 찾을 수 없습니다."
            }
        }

    }

})
