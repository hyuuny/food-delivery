package hyuuny.fooddelivery.presentation.admin.v1.optiongroup

import AdminOptionGroupSearchCondition
import CreateOptionGroupRequest
import ReorderOptionGroupRequests
import UpdateOptionGroupRequest
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.presentation.admin.v1.optiongroup.response.OptionGroupResponse
import hyuuny.fooddelivery.presentation.admin.v1.optiongroup.response.OptionGroupResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

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
        val responses = SimplePage(page.content.map { OptionGroupResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createOptionGroup(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateOptionGroupRequest>()

        val optionGroup = useCase.createOptionGroup(body) { menuUseCase.getMenu(body.menuId) }
        val response = OptionGroupResponse.from(optionGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getOptionGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val optionGroup = useCase.getOptionGroup(id)
        val response = OptionGroupResponse.from(optionGroup)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateOptionGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateOptionGroupRequest>()

        useCase.updateOptionGroup(id, body)
        return ok().buildAndAwait()
    }

    suspend fun reOrderOptionGroup(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<ReorderOptionGroupRequests>()

        useCase.reOrderOptionGroups(body) { menuUseCase.getMenu(body.menuId) }
        return ok().buildAndAwait()
    }

    suspend fun deleteOptionGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        useCase.deleteOptionGroup(id)
        return ok().buildAndAwait()
    }

}