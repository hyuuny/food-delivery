package hyuuny.fooddelivery.presentation.admin.v1.category

import AdminCategorySearchCondition
import CreateCategoryRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.category.CategoryUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.presentation.admin.v1.category.response.CategoryResponse
import hyuuny.fooddelivery.presentation.admin.v1.category.response.CategoryResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import parseBooleanQueryParam
import parseSort

@Component
class CategoryHandler(
    private val useCase: CategoryUseCase,
) {

    suspend fun getCategories(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val deliveryType = request.queryParamOrNull("deliveryType")
            ?.takeIf { it.isNotBlank() }
            ?.let { DeliveryType.valueOf(it.uppercase().trim()) }
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val visible = parseBooleanQueryParam(request.queryParamOrNull("visible"))
        val searchCondition = AdminCategorySearchCondition(id, deliveryType, name, visible)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getCategoriesByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { CategoryResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createCategory(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateCategoryRequest>()
        val category = useCase.createCategory(body)
        val response = CategoryResponse.from(category)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getCategory(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val category = useCase.getCategory(id)
        val response = CategoryResponse.from(category)
        return ok().bodyValueAndAwait(response)
    }

}