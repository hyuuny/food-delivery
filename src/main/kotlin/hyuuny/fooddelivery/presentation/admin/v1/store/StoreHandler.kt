package hyuuny.fooddelivery.presentation.admin.v1.store

import AdminStoreSearchCondition
import CreateStoreRequest
import UpdateStoreRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.store.StoreDetailUseCase
import hyuuny.fooddelivery.application.store.StoreImageUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.domain.store.DeliveryType
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreDetailResponse
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreImageResponse
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreResponse
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreResponses
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import parseSort
import java.time.LocalDateTime

@Component
class StoreHandler(
    private val useCase: StoreUseCase,
    private val storeDetailUseCase: StoreDetailUseCase,
    private val storeImageUseCase: StoreImageUseCase,
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

        val now = LocalDateTime.now()
        return coroutineScope {
            val storeDeferred = async { useCase.createStore(body, now) }

            val store = storeDeferred.await()
            val detailDeferred = async { storeDetailUseCase.createStoreDetail(store.id!!, body.storeDetail, now) }
            val imageDeferred =
                async { body.storeImage?.let { storeImageUseCase.createStoreImages(store.id!!, it, now) } }

            val storeDetailResponse = StoreDetailResponse.from(detailDeferred.await())
            val storeImageResponses = imageDeferred.await()?.map { StoreImageResponse.from(it) }
            val response = StoreResponse.from(store, storeDetailResponse, storeImageResponses)
            ok().bodyValueAndAwait(response)
        }
    }

    suspend fun getStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val store = useCase.getStore(id)
        val storeId = store.id!!
        return coroutineScope {
            val detailDeferred = async { storeDetailUseCase.getStoreDetailByStoreId(storeId) }
            val imageDeferred = async { storeImageUseCase.getStoreImagesByStoreId(storeId) }

            val storeDetailResponse = StoreDetailResponse.from(detailDeferred.await())
            val storeImageResponses = imageDeferred.await().map { StoreImageResponse.from(it) }
            val response = StoreResponse.from(store, storeDetailResponse, storeImageResponses)
            ok().bodyValueAndAwait(response)
        }
    }

    suspend fun updateStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateStoreRequest>()

        val now = LocalDateTime.now()
        return coroutineScope {
            val storeDeferred = async { useCase.updateStore(id, body, now) }
            val detailDeferred = async { storeDetailUseCase.updateStoreDetail(id, body.storeDetail, now) }
            val imageDeferred = async { body.storeImage?.let { storeImageUseCase.updateStoreImages(id, it, now) } }

            awaitAll(storeDeferred, detailDeferred, imageDeferred)
            ok().buildAndAwait()
        }
    }

    suspend fun deleteStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        return coroutineScope {
            val storeDeferred = async { useCase.deleteStore(id) }
            val detailDeferred = async { storeDetailUseCase.deleteStoreDetailByStoreId(id) }
            val imageDeferred = async { storeImageUseCase.deleteStoreImagesByStoreId(id) }

            awaitAll(storeDeferred, detailDeferred, imageDeferred)
            ok().buildAndAwait()
        }
    }

}