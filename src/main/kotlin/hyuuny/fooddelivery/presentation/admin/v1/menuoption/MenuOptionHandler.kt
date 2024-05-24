package hyuuny.fooddelivery.presentation.admin.v1.menuoption

import CreateMenuOptionRequest
import MenuOptionSearchCondition
import UpdateMenuOptionRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.application.menuoption.MenuOptionUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.presentation.admin.v1.menuoption.response.MenuOptionResponse
import hyuuny.fooddelivery.presentation.admin.v1.menuoption.response.MenuOptionResponses
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException
import parseSort

@Component
class MenuOptionHandler(
    private val useCase: MenuOptionUseCase,
    private val menuGroupUseCase: MenuGroupUseCase
) {

    suspend fun getMenuOptions(request: ServerRequest): ServerResponse {
        val menuGroupId = request.queryParamOrNull("menuGroupId")?.toLong()
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = MenuOptionSearchCondition(menuGroupId = menuGroupId, name = name)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getMenuOptions(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { MenuOptionResponses(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createMenuOption(request: ServerRequest): ServerResponse {
        val menuGroupId = request.pathVariable("menuGroupId").toLong()
        val body = request.awaitBody<CreateMenuOptionRequest>()

        if (!menuGroupUseCase.existsById(menuGroupId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 메뉴그룹입니다."
        )

        val menuOption = useCase.createMenuOption(body)
        return ok().bodyValueAndAwait(menuOption)
    }

    suspend fun getMenuOption(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val menuOption = useCase.getMenuOption(id)
        val response = MenuOptionResponse(menuOption)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateMenuOption(request: ServerRequest): ServerResponse {
        val menuGroupId = request.pathVariable("menuGroupId").toLong()
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateMenuOptionRequest>()

        if (!menuGroupUseCase.existsById(menuGroupId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 메뉴그룹입니다."
        )

        useCase.updateMenuOption(id, body)
        return ok().buildAndAwait()
    }

    suspend fun deleteMenuOption(request: ServerRequest): ServerResponse {
        val menuGroupId = request.pathVariable("menuGroupId").toLong()
        val id = request.pathVariable("id").toLong()

        if (!menuGroupUseCase.existsById(menuGroupId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 메뉴그룹입니다."
        )

        useCase.deleteMenuOption(id)
        return ok().buildAndAwait()
    }

}