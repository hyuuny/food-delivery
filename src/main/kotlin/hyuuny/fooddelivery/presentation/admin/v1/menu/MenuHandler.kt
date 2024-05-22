package hyuuny.fooddelivery.presentation.admin.v1.menu

import ChangeMenuStatusRequest
import CreateMenuRequest
import MenuResponse
import MenuResponses
import MenuSearchCondition
import UpdateMenuRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.domain.menu.MenuStatus
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import parseSort

@Component
class MenuHandler(
    private val useCase: MenuUseCase,
) {

    suspend fun getMenus(request: ServerRequest): ServerResponse {
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val status = request.queryParamOrNull("status")
            ?.takeIf { it.isNotBlank() }
            ?.let { MenuStatus.valueOf(it.uppercase().trim()) }

        val popularity = request.queryParamOrNull("popularity")?.let { popularity ->
            popularity.lowercase().takeIf { it == "true" || it == "false" }?.toBoolean()
        }

        val searchCondition = MenuSearchCondition(name = name, status = status, popularity = popularity)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getMenus(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { MenuResponses(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createMenu(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateMenuRequest>()
        val menu = useCase.createMenu(body)
        val response = MenuResponse(menu)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getMenu(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val menu = useCase.getMenu(id)
        val response = MenuResponse(menu)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateMeno(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateMenuRequest>()
        useCase.updateMenu(id, body)
        return ok().buildAndAwait()
    }

    suspend fun changeMenuStatus(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<ChangeMenuStatusRequest>()
        useCase.changeMenuStatus(id, body)
        return ok().buildAndAwait()
    }

    suspend fun deleteMenu(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        useCase.deleteMenu(id)
        return ok().buildAndAwait()
    }

}