package hyuuny.fooddelivery.likedstores.presentation.api.v1

import hyuuny.fooddelivery.likedstores.domain.LikedStore
import hyuuny.fooddelivery.likedstores.presentation.api.v1.response.LikedStoreResponses
import hyuuny.fooddelivery.reviews.application.ReviewUseCase
import hyuuny.fooddelivery.stores.application.StoreUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class LikedStoreResponseMapper(
    private val storeUseCase: StoreUseCase,
    private val reviewUseCase: ReviewUseCase,
) {

    suspend fun mapToLikedStoreResponses(likedStores: List<LikedStore>) = coroutineScope {
        val storeIds = likedStores.map { it.storeId }
        val storeDeferred = async { storeUseCase.getAllByIds(storeIds) }
        val averageScoreDeferred = async { reviewUseCase.getAverageScoreByStoreIds(storeIds) }

        val stores = storeDeferred.await()
        val averageScoreMap = averageScoreDeferred.await()

        val storeMap = stores.associateBy { it.id }

        likedStores.mapNotNull {
            val store = storeMap[it.storeId] ?: return@mapNotNull null
            val averageScore = averageScoreMap[store.id] ?: 0.0

            LikedStoreResponses.from(it, store, averageScore)
        }
    }

}
