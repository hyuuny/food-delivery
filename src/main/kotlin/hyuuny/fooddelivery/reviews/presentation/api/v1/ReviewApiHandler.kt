package hyuuny.fooddelivery.reviews.presentation.api.v1

import ApiReviewSearchCondition
import CreateReviewRequest
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.reviews.application.ReviewUseCase
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.users.application.UserUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class ReviewApiHandler(
    private val useCase: ReviewUseCase,
    private val userUseCase: UserUseCase,
    private val storeUseCase: StoreUseCase,
    private val orderUseCase: OrderUseCase,
    private val responseMapper: ReviewResponseMapper,
) {

    suspend fun getReviews(request: ServerRequest): ServerResponse {
        val userId = request.queryParamOrNull("userId")?.toLong()
        val storeId = request.queryParamOrNull("storeId")?.toLong()

        val searchCondition = ApiReviewSearchCondition(userId = userId, storeId = storeId)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getReviewByApiCondition(searchCondition, pageRequest)

        val reviewResponses = responseMapper.mapToReviewResponses(page.content)
        val response = SimplePage(reviewResponses, page)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun createReview(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val body = request.awaitBody<CreateReviewRequest>()

        val review = useCase.createReview(
            request = body,
            getUser = { userUseCase.getUser(userId) },
            getStore = storeUseCase::getStore,
            getOrder = orderUseCase::getOrder,
        )
        val response = responseMapper.mapToReviewResponse(review)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun deleteReview(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val id = request.pathVariable("id").toLong()

        useCase.deleteReview(id) { userUseCase.getUser(userId) }
        return ok().buildAndAwait()
    }

}
