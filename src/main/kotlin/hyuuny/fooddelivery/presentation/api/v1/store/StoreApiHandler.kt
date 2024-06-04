package hyuuny.fooddelivery.presentation.api.v1.store

import StoreApiSearchCondition
import extractCursorAndCount
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.application.store.StoreDetailUseCase
import hyuuny.fooddelivery.application.store.StoreImageUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.store.DeliveryType
import hyuuny.fooddelivery.presentation.api.v1.store.response.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import parseSort

@Component
class StoreApiHandler(
    private val useCase: StoreUseCase,
    private val storeDetailUseCase: StoreDetailUseCase,
    private val storeImageUseCase: StoreImageUseCase,
    private val menuGroupUseCase: MenuGroupUseCase,
    private val menuUseCase: MenuUseCase,
) {

    suspend fun getStores(request: ServerRequest): ServerResponse {
        val categoryId = request.queryParamOrNull("categoryId")?.toLong()
        val deliveryType = request.queryParamOrNull("deliveryType")
            ?.takeIf { it.isNotBlank() }
            ?.let { DeliveryType.valueOf(it.uppercase().trim()) }
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = StoreApiSearchCondition(
            categoryId = categoryId,
            deliveryType = deliveryType,
            name = name
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getStoresByApiSearchCondition(searchCondition, pageRequest)

        val menuGroups = menuGroupUseCase.getAllByStoreIds(page.content.mapNotNull { it.id })
        val menuGroupIds = menuGroups.groupBy { it.storeId }.values
            .mapNotNull { groups -> groups.minByOrNull { it.priority }?.id }

        val menuMap = menuUseCase.getAllByMenuGroupIds(menuGroupIds).groupBy { it.menuGroupId }

        val storeResponses = page.content.map { store ->
            val menuGroupResponses = menuGroupIds.mapNotNull { groupId ->
                menuGroups.find { it.id == groupId && it.storeId == store.id }?.let { menuGroup ->
                    val menuResponses = menuMap[menuGroup.id]?.map { menu -> MenuResponses.from(menu) } ?: emptyList()
                    MenuGroupResponses.from(menuGroup, menuResponses)
                }
            }
            StoreResponses.from(store, menuGroupResponses)
        }
        val responses = SimplePage(storeResponses, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getStore(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        return coroutineScope {
            val storeDeferred = async { useCase.getStore(id) }
            val store = storeDeferred.await()
            val storeId = store.id!!

            val detailDeferred = async { storeDetailUseCase.getStoreDetailByStoreId(storeId) }
            val imageDeferred = async { storeImageUseCase.getStoreImagesByStoreId(storeId) }

            val menuGroupDeferred = async { menuGroupUseCase.getAllByStoreId(storeId) }

            val menuGroups = menuGroupDeferred.await().sortedWith(compareBy { it.priority })
            val menuDeferred = async { menuUseCase.getAllByMenuGroupIds(menuGroups.mapNotNull { it.id }) }

            val storeDetail = detailDeferred.await()
            val storeImages = imageDeferred.await()
            val menus = menuDeferred.await().sortedWith(compareByDescending<Menu> { it.popularity }.thenBy { it.price })
            val menusMap = menus.groupBy { it.menuGroupId }

            val menuGroupResponses = menuGroups.map { menuGroup ->
                val menusOfGroup = menusMap[menuGroup.id] ?: emptyList()
                MenuGroupResponse.from(menuGroup, menusOfGroup.map { MenuResponse.from(it) })
            }

            val response = StoreResponse.from(
                entity = store,
                storeDetail = StoreDetailResponse.from(storeDetail),
                storeImages = storeImages.map { StoreImageResponse.from(it) },
                menuGroups = menuGroupResponses
            )
            ok().bodyValueAndAwait(response)
        }
    }

}