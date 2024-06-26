package hyuuny.fooddelivery.presentation.api.v1.store

import ApiStoreSearchCondition
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.response.SimplePage
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
class StoreApiHandler(
    private val useCase: StoreUseCase,
    private val responseMapper: StoreResponseMapper,
) {

    suspend fun getStores(request: ServerRequest): ServerResponse {
        val categoryId = request.queryParamOrNull("categoryId")?.toLong()
        val deliveryType = request.queryParamOrNull("deliveryType")
            ?.takeIf { it.isNotBlank() }
            ?.let { DeliveryType.valueOf(it.uppercase().trim()) }
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = ApiStoreSearchCondition(
            categoryId = categoryId,
            deliveryType = deliveryType,
            name = name
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getStoresByApiCondition(searchCondition, pageRequest)
        val storeResponses = responseMapper.mapToStoreResponses(page.content)
        val responses = SimplePage(storeResponses, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val store = useCase.getStore(id)
        val response = responseMapper.mapToStoreResponse(store)
        return ok().bodyValueAndAwait(response)
    }

}