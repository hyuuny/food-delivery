package hyuuny.fooddelivery.presentation.api.v1.menu

import hyuuny.fooddelivery.application.menu.MenuUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class MenuApiHandler(
    private val useCase: MenuUseCase,
    private val responseMapper: MenuResponseMapper,
) {

    suspend fun getMenu(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val menu = useCase.getMenu(id)
        val response = responseMapper.mepToMenuResponse(menu)
        return ok().bodyValueAndAwait(response)
    }

}
