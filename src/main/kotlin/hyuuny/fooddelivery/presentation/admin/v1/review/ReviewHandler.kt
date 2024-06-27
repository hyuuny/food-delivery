package hyuuny.fooddelivery.presentation.admin.v1.review

import AdminReviewSearchCondition
import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.convertToLocalDate
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class ReviewHandler(
    private val useCase: ReviewUseCase,
    private val responseMapper: ReviewResponseMapper,
) {

    suspend fun getReviews(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val userId = request.queryParamOrNull("userId")?.toLong()
        val userName = request.queryParamOrNull("userName")?.takeIf { it.isNotBlank() }
        val userNickname = request.queryParamOrNull("userNickname")?.takeIf { it.isNotBlank() }
        val storeId = request.queryParamOrNull("storeId")?.toLong()
        val storeName = request.queryParamOrNull("storeName")?.takeIf { it.isNotBlank() }
        val orderId = request.queryParamOrNull("orderId")?.toLong()
        val fromDate = request.queryParamOrNull("fromDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val toDate = request.queryParamOrNull("toDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }

        val searchCondition = AdminReviewSearchCondition(
            id = id,
            userId = userId,
            userName = userName,
            userNickname = userNickname,
            storeId = storeId,
            storeName = storeName,
            orderId = orderId,
            fromDate = fromDate,
            toDate = toDate,
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getReviewByAdminCondition(searchCondition, pageRequest)
        val reviewResponses = responseMapper.mepToReviewResponses(page.content)
        val responses = SimplePage(reviewResponses, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getReview(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val review = useCase.getReview(id)
        val response = responseMapper.mapToReviewResponse(review)
        return ok().bodyValueAndAwait(response)
    }

}
