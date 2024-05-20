package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import CreateMenuGroupRequest
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.presentation.admin.v1.menugroup.response.MenuGroupResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.server.ResponseStatusException

@Component
class MenuGroupHandler(
    private val useCase: MenuGroupUseCase,
    private val menuUseCase: MenuUseCase,
) {

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