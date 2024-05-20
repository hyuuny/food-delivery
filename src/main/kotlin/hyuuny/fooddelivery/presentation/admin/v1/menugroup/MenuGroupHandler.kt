package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import CreateMenuGroupRequest
import MenuGroupSearchCondition
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.presentation.admin.v1.menugroup.response.MenuGroupResponse
import hyuuny.fooddelivery.presentation.admin.v1.menugroup.response.MenuGroupResponses
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException

@Component
class MenuGroupHandler(
    private val useCase: MenuGroupUseCase,
    private val menuUseCase: MenuUseCase,
) {

    suspend fun getMenuGroups(request: ServerRequest): ServerResponse {
        val menuId = request.queryParamOrNull("menuId")?.toLong()
        val name = request.queryParamOrNull("name")

        val searchCondition = MenuGroupSearchCondition(menuId = menuId, name = name)
        val cursor = request.queryParamOrNull("cursor")?.toIntOrNull() ?: 0
        val count = request.queryParamOrNull("count")?.toIntOrNull() ?: 15

        val sortParam = request.queryParamOrNull("sort")
        val sort = sortParam?.let {
            val splitParam = it.split(":")
            val property = splitParam[0]
            val direction = if (splitParam.getOrNull(1) == "asc") Direction.ASC else Direction.DESC
            Sort.by(direction, property)
        } ?: Sort.by(Direction.DESC, "id")

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getMenuGroups(searchCondition, pageRequest)
        val response = PageImpl(page.content.map { MenuGroupResponses(it) }, pageRequest, page.totalElements)
        return ok().bodyValueAndAwait(response)
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

}