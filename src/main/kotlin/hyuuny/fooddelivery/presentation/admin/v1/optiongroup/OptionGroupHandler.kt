package hyuuny.fooddelivery.presentation.admin.v1.optiongroup

import AdminOptionGroupSearchCondition
import CreateOptionGroupRequest
import ReorderOptionGroupRequests
import UpdateOptionGroupRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.presentation.admin.v1.optiongroup.response.OptionGroupResponse
import hyuuny.fooddelivery.presentation.admin.v1.optiongroup.response.OptionGroupResponses
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException
import parseSort

@Component
class OptionGroupHandler(
    private val useCase: OptionGroupUseCase,
    private val menuUseCase: MenuUseCase,
) {

    suspend fun getOptionGroups(request: ServerRequest): ServerResponse {
        val menuId = request.queryParamOrNull("menuId")?.toLong()
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = AdminOptionGroupSearchCondition(menuId = menuId, name = name)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getOptionGroupsByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { OptionGroupResponses(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createOptionGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val body = request.awaitBody<CreateOptionGroupRequest>()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        val optionGroup = useCase.createOptionGroup(body)
        val response = OptionGroupResponse(optionGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getOptionGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val optionGroup = useCase.getOptionGroup(id)
        val response = OptionGroupResponse(optionGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateOptionGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateOptionGroupRequest>()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        useCase.updateOptionGroup(id, body)
        return ok().buildAndAwait()
    }

    suspend fun reOrderOptionGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val body = request.awaitBody<ReorderOptionGroupRequests>()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        useCase.reOrderOptionGroups(menuId, body)
        return ok().buildAndAwait()
    }

    suspend fun deleteOptionGroup(request: ServerRequest): ServerResponse {
        val menuId = request.pathVariable("menuId").toLong()
        val id = request.pathVariable("id").toLong()

        if (!menuUseCase.existById(menuId)) throw ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다.")

        useCase.deleteOptionGroup(id)
        return ok().buildAndAwait()
    }

}