package hyuuny.fooddelivery.stores.presentation.api.v1

import hyuuny.fooddelivery.menugroups.application.MenuGroupUseCase
import hyuuny.fooddelivery.menus.application.MenuUseCase
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.reviews.application.ReviewUseCase
import hyuuny.fooddelivery.stores.application.StoreDetailUseCase
import hyuuny.fooddelivery.stores.application.StoreImageUseCase
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.presentation.api.v1.response.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import truncateToSingleDecimalPlace

@Component
class StoreResponseMapper(
    private val storeDetailUseCase: StoreDetailUseCase,
    private val storeImageUseCase: StoreImageUseCase,
    private val menuGroupUseCase: MenuGroupUseCase,
    private val menuUseCase: MenuUseCase,
    private val reviewUseCase: ReviewUseCase,
) {

    suspend fun mapToStoreResponse(store: Store) = coroutineScope {
        val storeId = store.id!!

        val detailDeferred = async { storeDetailUseCase.getStoreDetailByStoreId(storeId) }
        val imageDeferred = async { storeImageUseCase.getStoreImagesByStoreId(storeId) }

        val menuGroupDeferred = async { menuGroupUseCase.getAllByStoreId(storeId) }
        val menuGroups = menuGroupDeferred.await().sortedWith(compareBy { it.priority })

        val menuDeferred = async { menuUseCase.getAllByMenuGroupIds(menuGroups.mapNotNull { it.id }) }
        val averageScoreDeferred = async { reviewUseCase.getAverageScoreByStoreIds(listOf(storeId)) }

        val storeDetail = detailDeferred.await()
        val storeImages = imageDeferred.await()
        val averageScoreMap = averageScoreDeferred.await()

        val menus = menuDeferred.await().sortedWith(compareByDescending<Menu> { it.popularity }.thenBy { it.price })
        val menusMap = menus.groupBy { it.menuGroupId }

        val menuGroupResponses = menuGroups.map { menuGroup ->
            val menusOfGroup = menusMap[menuGroup.id] ?: emptyList()
            MenuGroupResponse.from(menuGroup, menusOfGroup.map { MenuResponse.from(it) })
        }

        val averageScore = averageScoreMap[store.id] ?: 0.0
        val truncatedAverageScore = truncateToSingleDecimalPlace(averageScore)

        StoreResponse.from(
            entity = store,
            averageScore = truncatedAverageScore,
            storeDetail = StoreDetailResponse.from(storeDetail),
            storeImages = storeImages.map { StoreImageResponse.from(it) },
            menuGroups = menuGroupResponses
        )
    }

    suspend fun mapToStoreResponses(stores: List<Store>) = coroutineScope {
        val storeIds = stores.mapNotNull { it.id }
        val menuGroups = menuGroupUseCase.getAllByStoreIds(storeIds)
        val menuGroupIds = menuGroups.groupBy { it.storeId }.values
            .mapNotNull { groups -> groups.minByOrNull { it.priority }?.id }

        val menuDeferred = async { menuUseCase.getAllByMenuGroupIds(menuGroupIds) }
        val averageScoreDeferred = async { reviewUseCase.getAverageScoreByStoreIds(storeIds) }

        val menuMap = menuDeferred.await().groupBy { it.menuGroupId }
        val averageScoreMap = averageScoreDeferred.await()

        stores.map { store ->
            val menuGroupResponses = menuGroupIds.mapNotNull { groupId ->
                menuGroups.find { it.id == groupId && it.storeId == store.id }?.let { menuGroup ->
                    val menuResponses = menuMap[menuGroup.id]?.map { menu -> MenuResponses.from(menu) } ?: emptyList()
                    MenuGroupResponses.from(menuGroup, menuResponses)
                }
            }
            val averageScore = averageScoreMap[store.id] ?: 0.0
            StoreResponses.from(store, truncateToSingleDecimalPlace(averageScore), menuGroupResponses)
        }
    }

}
