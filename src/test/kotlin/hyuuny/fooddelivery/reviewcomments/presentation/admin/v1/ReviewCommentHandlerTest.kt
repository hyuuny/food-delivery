package hyuuny.fooddelivery.reviewcomments.presentation.admin.v1

import ChangeContentRequest
import CreateReviewCommentRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.reviewcomments.application.ReviewCommentUseCase
import hyuuny.fooddelivery.reviewcomments.domain.ReviewComment
import hyuuny.fooddelivery.reviewcomments.infrastructure.ReviewCommentRepository
import hyuuny.fooddelivery.reviewcomments.presentation.admin.v1.response.ReviewCommentResponse
import hyuuny.fooddelivery.reviews.application.ReviewUseCase
import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class ReviewCommentHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: ReviewCommentUseCase

    @MockkBean
    private lateinit var userUseCase: UserUseCase

    @MockkBean
    private lateinit var reviewUseCase: ReviewUseCase

    @MockkBean
    private lateinit var repository: ReviewCommentRepository

    @DisplayName("회원이 작성한 리뷰에 댓글을 달 수 있다.")
    @Test
    fun createReviewComment() {
        val userId = 1L
        val reviewId = 1L

        val request = CreateReviewCommentRequest(
            userId = userId,
            reviewId = reviewId,
            content = "감사합니다. 다음에 또 방문해주세요.\uD83D\uDE00\uD83D\uDE03",
        )

        val now = LocalDateTime.now()
        val reviewComment = generateReviewComment(request, now)

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

        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { reviewUseCase.getReview(any()) } returns review
        coEvery { useCase.createReviewComment(any(), any(), any()) } returns reviewComment

        webTestClient.post().uri("/admin/v1/review-comments")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(reviewComment.id!!)
            .jsonPath("$.reviewId").isEqualTo(reviewComment.reviewId)
            .jsonPath("$.userId").isEqualTo(reviewComment.userId)
            .jsonPath("$.ownerName").isEqualTo(reviewComment.getOwnerName())
            .jsonPath("$.ownerImageUrl").isEqualTo(reviewComment.getOwnerImageUrl())
            .jsonPath("$.content").isEqualTo(reviewComment.content)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("리뷰에 작성한 댓글 목록을 조회할 수 있다.")
    @Test
    fun getReviewComments() {
        val userId = 1L
        val reviewIds = listOf(1L, 2L, 3L, 4L)

        val now = LocalDateTime.now()
        val reviewComments = reviewIds.mapIndexed { idx, it ->
            ReviewComment(
                id = idx.toLong() + 1,
                reviewId = it,
                userId = userId,
                content = "벌써 ${it}번쨰 방문이시네요.\n항상 감사합니다. 다음에 또 방문해주세요.\uD83D\uDE00\uD83D\uDE03",
                createdAt = now,
                updatedAt = now,
            )
        }
        val sortedReviewComments = reviewComments.sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(sortedReviewComments, pageable, reviewComments.size.toLong())
        coEvery { useCase.getReviewCommentByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/review-comments?sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content").isArray
            .jsonPath("$.content.length()").isEqualTo(reviewComments.size)
            .jsonPath("$.content[0].id").isEqualTo(reviewComments[3].id!!)
            .jsonPath("$.content[0].userId").isEqualTo(reviewComments[3].userId)
            .jsonPath("$.content[0].reviewId").isEqualTo(reviewComments[3].reviewId)

            .jsonPath("$.content[1].id").isEqualTo(reviewComments[2].id!!)
            .jsonPath("$.content[1].userId").isEqualTo(reviewComments[2].userId)
            .jsonPath("$.content[1].reviewId").isEqualTo(reviewComments[2].reviewId)

            .jsonPath("$.content[2].id").isEqualTo(reviewComments[1].id!!)
            .jsonPath("$.content[2].userId").isEqualTo(reviewComments[1].userId)
            .jsonPath("$.content[2].reviewId").isEqualTo(reviewComments[1].reviewId)

            .jsonPath("$.content[3].id").isEqualTo(reviewComments[0].id!!)
            .jsonPath("$.content[3].userId").isEqualTo(reviewComments[0].userId)
            .jsonPath("$.content[3].reviewId").isEqualTo(reviewComments[0].reviewId)

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
    }

    @DisplayName("리뷰 댓글을 상세조회 할 수 있다.")
    @Test
    fun getReviewComment() {
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
        coEvery { useCase.getReviewComment(any()) } returns reviewComment

        val expectedResponse = ReviewCommentResponse.from(reviewComment)
        webTestClient.get().uri("/admin/v1/review-comments/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.reviewId").isEqualTo(expectedResponse.reviewId)
            .jsonPath("$.ownerName").isEqualTo(expectedResponse.ownerName)
            .jsonPath("$.ownerImageUrl").isEqualTo(expectedResponse.ownerImageUrl)
            .jsonPath("$.content").isEqualTo(expectedResponse.content)
    }

    @DisplayName("리뷰 댓글의 내용을 수정할 수 있다.")
    @Test
    fun changeContent() {
        val reviewId = 1L
        val request = ChangeContentRequest(content = "행복한 하루되세요")
        coEvery { useCase.changeContent(any(), any()) } returns Unit

        webTestClient.patch().uri("/admin/v1/review-comments/$reviewId/change-content")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("리뷰 댓글을 삭제할 수 있다.")
    @Test
    fun deleteReviewComment() {
        val id = 1L
        coEvery { useCase.deleteReviewComment(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/review-comments/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.message").isEqualTo("${id}번 리뷰 댓글이 정상적으로 삭제되었습니다.")
    }

    private fun generateReviewComment(request: CreateReviewCommentRequest, now: LocalDateTime): ReviewComment {
        return ReviewComment(
            id = 1L,
            reviewId = request.reviewId,
            userId = request.userId,
            content = request.content,
            createdAt = now,
            updatedAt = now,
        )
    }

}

