package hyuuny.fooddelivery.presentation.admin.v1.category

import CreateCategoryRequest
import hyuuny.fooddelivery.application.category.CategoryUseCase
import hyuuny.fooddelivery.presentation.admin.v1.category.response.CategoryResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class CategoryHandler(
    private val useCase: CategoryUseCase,
) {

    suspend fun createCategory(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateCategoryRequest>()
        val category = useCase.createCategory(body)
        val response = CategoryResponse.from(category)
        return ok().bodyValueAndAwait(response)
    }

}