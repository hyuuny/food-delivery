package hyuuny.fooddelivery.stores.presentation.admin.v1

import AdminStoreSearchCondition
import CreateStoreRequest
import UpdateStoreRequest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.stores.presentation.admin.v1.response.StoreResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class StoreHandler(
    private val useCase: StoreUseCase,
    private val responseMapper: StoreResponseMapper,
) {

    suspend fun getStores(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val categoryId = request.queryParamOrNull("categoryId")?.toLong()
        val deliveryType = request.queryParamOrNull("deliveryType")
            ?.takeIf { it.isNotBlank() }
            ?.let { DeliveryType.valueOf(it.uppercase().trim()) }
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val taxId = request.queryParamOrNull("taxId")?.takeIf { it.isNotBlank() }
        val phoneNumber = request.queryParamOrNull("phoneNumber")?.takeIf { it.isNotBlank() }
        val searchCondition = AdminStoreSearchCondition(id, categoryId, deliveryType, name, taxId, phoneNumber)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getStoresByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { StoreResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createStore(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateStoreRequest>()

        val store = useCase.createStore(body)
        val response = responseMapper.mapToStoreResponse(store)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val store = useCase.getStore(id)
        val response = responseMapper.mapToStoreResponse(store)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateStoreRequest>()

        val store = useCase.updateStore(id, body)
        val response = responseMapper.mapToStoreResponse(store)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun deleteStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        useCase.deleteStore(id)
        return ok().buildAndAwait()
    }

}
