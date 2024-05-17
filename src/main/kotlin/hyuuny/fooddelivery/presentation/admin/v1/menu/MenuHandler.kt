package hyuuny.fooddelivery.presentation.admin.v1.menu

import ChangeMenuStatusRequest
import CreateMenuRequest
import MenuResponse
import UpdateMenuRequest
import hyuuny.fooddelivery.application.menu.MenuUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class MenuHandler(
    private val useCase: MenuUseCase,
) {

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