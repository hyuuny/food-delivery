package hyuuny.fooddelivery.presentation.api.v1.likedstore

import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.domain.likedstore.LikedStore
import hyuuny.fooddelivery.presentation.api.v1.likedstore.response.LikedStoreResponses
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
