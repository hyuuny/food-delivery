package hyuuny.fooddelivery.presentation.admin.v1.menu

import ChangeMenuStatusRequest
import CreateMenuRequest
import MenuResponse
import MenuResponses
import MenuSearchCondition
import UpdateMenuRequest
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.domain.menu.MenuStatus
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class MenuHandler(
    private val useCase: MenuUseCase,
) {

    suspend fun getMenus(request: ServerRequest): ServerResponse {
        val name = request.queryParamOrNull("name")
        val status = request.queryParamOrNull("status")
            ?.takeIf { it.isNotBlank() }
            ?.let { MenuStatus.valueOf(it.uppercase().trim()) }

        val popularity = request.queryParamOrNull("popularity")?.toBoolean()

        val searchCondition = MenuSearchCondition(name = name, status = status, popularity = popularity)
        val cursor = request.queryParamOrNull("cursor")?.toIntOrNull() ?: 0
        val count = request.queryParamOrNull("count")?.toIntOrNull() ?: 15

        val sortParam = request.queryParamOrNull("sort")
        val sort: Sort = sortParam?.let {
            val splitParam = it.split(":")
            val property = splitParam[0]
            val direction = if (splitParam.getOrNull(1) == "asc") Sort.Direction.ASC else Sort.Direction.DESC
            Sort.by(direction, property)
        } ?: Sort.by(Sort.Direction.DESC, "id")

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getMenus(searchCondition, pageRequest)
        val responses = PageImpl(page.content.map { MenuResponses(it) }, pageRequest, page.totalElements)
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