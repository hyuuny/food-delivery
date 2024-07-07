package hyuuny.fooddelivery.presentation.api.v1.likedstore

import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.domain.likedstore.LikedStore
import hyuuny.fooddelivery.presentation.api.v1.likedstore.response.LikedStoreResponses
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class LikedStoreResponseMapper(
    private val storeUseCase: StoreUseCase,
) {

    suspend fun mapToLikedStoreResponses(likedStores: List<LikedStore>) = coroutineScope {
        val storeIds = likedStores.map { it.storeId }
        val storeDeferred = async { storeUseCase.getAllByIds(storeIds) }

        val stores = storeDeferred.await()
        val storeMap = stores.associateBy { it.id }

        likedStores.mapNotNull {
            val store = storeMap[it.storeId] ?: return@mapNotNull null
            LikedStoreResponses.from(it, store)
        }
    }

}
