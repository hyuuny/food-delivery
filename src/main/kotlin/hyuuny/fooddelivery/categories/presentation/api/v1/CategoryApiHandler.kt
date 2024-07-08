package hyuuny.fooddelivery.categories.presentation.api.v1

import hyuuny.fooddelivery.categories.application.CategoryUseCase
import hyuuny.fooddelivery.categories.presentation.api.v1.response.CategoryResponses
import hyuuny.fooddelivery.common.constant.DeliveryType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class CategoryApiHandler(
    private val useCase: CategoryUseCase,
) {

    suspend fun getVisibleCategoriesByDeliveryTypeOrderByPriority(request: ServerRequest): ServerResponse {
        val deliveryType = request.pathVariable("deliveryType").let { DeliveryType.valueOf(it.uppercase().trim()) }

        val categories = useCase.getVisibleCategoriesByDeliveryTypeOrderByPriority(deliveryType)
        val responses = categories.map { CategoryResponses.from(it) }
        return ok().bodyValueAndAwait(responses)
    }

}
