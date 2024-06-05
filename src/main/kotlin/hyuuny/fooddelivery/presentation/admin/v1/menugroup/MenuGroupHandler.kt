package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import AdminMenuGroupSearchCondition
import CreateMenuGroupRequest
import MenuGroupResponse
import MenuGroupResponses
import ReorderMenuGroupRequests
import UpdateMenuGroupRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException
import parseSort

@Component
class MenuGroupHandler(
    private val useCase: MenuGroupUseCase
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
        val responses = SimplePage(page.content.map { MenuGroupResponses(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createMenuGroup(request: ServerRequest): ServerResponse {
        val storeId = request.pathVariable("storeId").toLong()
        val body = request.awaitBody<CreateMenuGroupRequest>()

        if (storeId != body.storeId) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "매장 아이디가 일치하지 않습니다.")

        val menuGroup = useCase.createMenuGroup(
            CreateMenuGroupRequest(
                storeId = storeId,
                name = body.name,
                priority = body.priority,
                description = body.description
            )
        )
        val response = MenuGroupResponse(menuGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateMenuGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateMenuGroupRequest>()

        useCase.updateMenuGroup(id, body)
        return ok().buildAndAwait()
    }

    suspend fun reOrderMenuGroup(request: ServerRequest): ServerResponse {
        val storeId = request.pathVariable("storeId").toLong()
        val body = request.awaitBody<ReorderMenuGroupRequests>()

        useCase.reOrderMenuGroups(storeId, body)
        return ok().buildAndAwait()
    }

    suspend fun getMenuGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val menuGroup = useCase.getMenuGroup(id)
        val response = MenuGroupResponse(menuGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun deleteMenuGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        useCase.deleteMenuGroup(id)
        return ok().buildAndAwait()
    }

}
