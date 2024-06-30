package hyuuny.fooddelivery.application.reviewcomment

import ChangeContentRequest
import hyuuny.fooddelivery.domain.reviewcomment.ReviewComment
import hyuuny.fooddelivery.infrastructure.reviewcomment.ReviewCommentRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeReviewCommentContentUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewCommentRepository>()
    val useCase = ReviewCommentUseCase(repository)

    Given("리뷰 댓글을 수정 할 때") {
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
        val request = ChangeContentRequest(content = "행복한 하루되세요")

        coEvery { repository.findById(any()) } returns reviewComment
        coEvery { repository.updateContent(any()) } returns Unit

        `when`("존재하는 리뷰 댓글이라면") {
            val result = useCase.changeContent(id, request)

            then("댓글 내용을 수정할 수 있다.") {
                coVerify { repository.updateContent(any()) }
            }
        }

        `when`("존재하지 않는 리뷰 댓글이라면") {
            coEvery { repository.findById(any()) } returns null

            then("리뷰 댓글을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeContent(0, request)
                }
                ex.message shouldBe "0번 리뷰 댓글을 찾을 수 없습니다."
            }
        }

    }

})
