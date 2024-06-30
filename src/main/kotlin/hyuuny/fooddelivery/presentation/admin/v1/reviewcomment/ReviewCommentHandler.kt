package hyuuny.fooddelivery.presentation.admin.v1.reviewcomment

import AdminReviewCommentSearchCondition
import ChangeContentRequest
import CreateReviewCommentRequest
import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.application.reviewcomment.ReviewCommentUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.convertToLocalDate
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.presentation.admin.v1.reviewcomment.response.ReviewCommentResponse
import hyuuny.fooddelivery.presentation.admin.v1.reviewcomment.response.ReviewCommentResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class ReviewCommentHandler(
    private val useCase: ReviewCommentUseCase,
    private val userUseCase: UserUseCase,
    private val reviewUseCase: ReviewUseCase,
) {

    suspend fun getReviewComments(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val userId = request.queryParamOrNull("userId")?.toLong()
        val reviewId = request.queryParamOrNull("reviewId")?.toLong()
        val fromDate = request.queryParamOrNull("fromDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val toDate = request.queryParamOrNull("toDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }

        val searchCondition = AdminReviewCommentSearchCondition(
            id = id,
            userId = userId,
            reviewId = reviewId,
            fromDate = fromDate,
            toDate = toDate,
        )
        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getReviewCommentByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { ReviewCommentResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createReviewComment(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateReviewCommentRequest>()

        val reviewComment = useCase.createReviewComment(
            request = body,
            getOwner = userUseCase::getUser,
            getReview = reviewUseCase::getReview,
        )
        val response = ReviewCommentResponse.from(reviewComment)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getReviewComment(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val comment = useCase.getReviewComment(id)
        val response = ReviewCommentResponse.from(comment)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun changeContent(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<ChangeContentRequest>()

        useCase.changeContent(id, body)
        return ok().buildAndAwait()
    }

}
