package hyuuny.fooddelivery.presentation.admin.v1.menuoption

import CreateMenuOptionRequest
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.application.menuoption.MenuOptionUseCase
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.server.ResponseStatusException

@Component
class MenuOptionHandler(
    private val useCase: MenuOptionUseCase,
    private val menuGroupUseCase: MenuGroupUseCase
) {

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

}