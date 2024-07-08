package hyuuny.fooddelivery.stores.presentation.admin.v1

import hyuuny.fooddelivery.stores.application.StoreDetailUseCase
import hyuuny.fooddelivery.stores.application.StoreImageUseCase
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.presentation.admin.v1.response.StoreDetailResponse
import hyuuny.fooddelivery.stores.presentation.admin.v1.response.StoreImageResponse
import hyuuny.fooddelivery.stores.presentation.admin.v1.response.StoreResponse
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
