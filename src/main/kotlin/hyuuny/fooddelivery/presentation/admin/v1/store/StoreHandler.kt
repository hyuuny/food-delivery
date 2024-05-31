package hyuuny.fooddelivery.presentation.admin.v1.store

import CreateStoreRequest
import hyuuny.fooddelivery.application.store.StoreDetailUseCase
import hyuuny.fooddelivery.application.store.StoreImageUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreDetailResponse
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreImageResponse
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.time.LocalDateTime

@Component
class StoreHandler(
    private val useCase: StoreUseCase,
    private val storeDetailUseCase: StoreDetailUseCase,
    private val storeImageUseCase: StoreImageUseCase,
) {

    suspend fun createStore(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateStoreRequest>()

        val now = LocalDateTime.now()
        return coroutineScope {
            val storeDeferred = async { useCase.createStore(body, now) }

            val store = storeDeferred.await()
            val detailDeferred = async { storeDetailUseCase.createStoreDetail(store.id!!, body.storeDetail, now) }
            val imageDeferred = async { storeImageUseCase.createStoreImages(store.id!!, body.storeImage, now) }

            val storeDetailResponse = StoreDetailResponse.from(detailDeferred.await())
            val storeImageResponses = imageDeferred.await().map { StoreImageResponse.from(it) }
            val response = StoreResponse.from(store, storeDetailResponse, storeImageResponses)
            ok().bodyValueAndAwait(response)
        }
    }

}