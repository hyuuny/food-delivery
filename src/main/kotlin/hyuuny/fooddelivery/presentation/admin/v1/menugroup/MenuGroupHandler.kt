package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import CreateMenuGroupRequest
import MenuGroupSearchCondition
import ReorderMenuGroupRequests
import UpdateMenuGroupRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.presentation.admin.v1.menugroup.response.MenuGroupResponse
import hyuuny.fooddelivery.presentation.admin.v1.menugroup.response.MenuGroupResponses
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException
import parseSort

@Component
class MenuGroupHandler(
    private val useCase: MenuGroupUseCase,
    private val menuUseCase: MenuUseCase,
) {

    suspend fun getMenuGroups(request: ServerRequest): ServerResponse {
        val menuId = request.queryParamOrNull("menuId")?.toLong()
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = MenuGroupSearchCondition(menuId = menuId, name = name)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getMenuGroups(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { MenuGroupResponses(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createMenuGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val body = request.awaitBody<CreateMenuGroupRequest>()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        val menuGroup = useCase.createMenuGroup(body)
        val response = MenuGroupResponse(menuGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getMenuGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val menuGroup = useCase.getMenuGroup(id)
        val response = MenuGroupResponse(menuGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateMenuGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateMenuGroupRequest>()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        useCase.updateMenuGroup(id, body)
        return ok().buildAndAwait()
    }

    suspend fun reOrderMenuGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val body = request.awaitBody<ReorderMenuGroupRequests>()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        useCase.reOrderMenuGroups(menuId, body)
        return ok().buildAndAwait()
    }

    suspend fun deleteMenuGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val id = request.pathVariable("id").toLong()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        useCase.deleteMenuGroup(id)
        return ok().buildAndAwait()
    }

}