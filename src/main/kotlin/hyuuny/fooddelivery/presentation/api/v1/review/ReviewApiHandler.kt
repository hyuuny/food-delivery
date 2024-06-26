package hyuuny.fooddelivery.presentation.api.v1.review

import CreateReviewRequest
import hyuuny.fooddelivery.application.order.OrderUseCase
import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class ReviewApiHandler(
    private val useCase: ReviewUseCase,
    private val userUseCase: UserUseCase,
    private val storeUseCase: StoreUseCase,
    private val orderUseCase: OrderUseCase,
    private val responseMapper: ReviewResponseMapper,
) {

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

}
