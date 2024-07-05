package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import AdminMenuGroupSearchCondition
import CreateMenuGroupRequest
import MenuGroupResponse
import MenuGroupResponses
import ReorderMenuGroupRequests
import UpdateMenuGroupRequest
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class MenuGroupHandler(
    private val useCase: MenuGroupUseCase,
    private val storeUseCase: StoreUseCase,
) {

    suspend fun getMenuGroups(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val storeId = request.queryParamOrNull("storeId")?.toLong()
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = AdminMenuGroupSearchCondition(id = id, storeId = storeId, name = name)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getMenuGroupsByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { MenuGroupResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createMenuGroup(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateMenuGroupRequest>()

        val menuGroup = useCase.createMenuGroup(body) { storeUseCase.getStore(body.storeId) }
        val response = MenuGroupResponse.from(menuGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getMenuGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val menuGroup = useCase.getMenuGroup(id)
        val response = MenuGroupResponse.from(menuGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateMenuGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateMenuGroupRequest>()

        useCase.updateMenuGroup(id, body)
        return ok().buildAndAwait()
    }

    suspend fun reOrderMenuGroup(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<ReorderMenuGroupRequests>()

        useCase.reOrderMenuGroups(body) { storeUseCase.getStore(body.storeId) }
        return ok().buildAndAwait()
    }

    suspend fun deleteMenuGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        useCase.deleteMenuGroup(id)
        return ok().buildAndAwait()
    }

}
