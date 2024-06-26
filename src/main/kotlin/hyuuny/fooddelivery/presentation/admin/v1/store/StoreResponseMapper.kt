package hyuuny.fooddelivery.presentation.admin.v1.store

import hyuuny.fooddelivery.application.store.StoreDetailUseCase
import hyuuny.fooddelivery.application.store.StoreImageUseCase
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreDetailResponse
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreImageResponse
import hyuuny.fooddelivery.presentation.admin.v1.store.response.StoreResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component(value = "adminStoreResponseMapper")
class StoreResponseMapper(
    private val storeDetailUseCase: StoreDetailUseCase,
    private val storeImageUseCase: StoreImageUseCase,
) {

    suspend fun mapToStoreResponse(store: Store): StoreResponse = coroutineScope {
        val detailDeferred = async { storeDetailUseCase.getStoreDetailByStoreId(store.id!!) }
        val imageDeferred = async { storeImageUseCase.getStoreImagesByStoreId(store.id!!) }

        val storeDetailResponse = StoreDetailResponse.from(detailDeferred.await())
        val storeImageResponses = imageDeferred.await().map { StoreImageResponse.from(it) }
        StoreResponse.from(store, storeDetailResponse, storeImageResponses)
    }

}