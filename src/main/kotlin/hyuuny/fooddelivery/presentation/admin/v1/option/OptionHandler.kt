package hyuuny.fooddelivery.presentation.admin.v1.option

import AdminOptionSearchCondition
import ChangeOptionGroupIdRequest
import CreateOptionRequest
import UpdateOptionRequest
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.presentation.admin.v1.option.response.OptionResponse
import hyuuny.fooddelivery.presentation.admin.v1.option.response.OptionResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class OptionHandler(
    private val useCase: OptionUseCase,
    private val optionGroupUseCase: OptionGroupUseCase
) {

    suspend fun getOptions(request: ServerRequest): ServerResponse {
        val optionGroupId = request.queryParamOrNull("optionGroupId")?.toLong()
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = AdminOptionSearchCondition(optionGroupId = optionGroupId, name = name)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getOptionsByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { OptionResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createOption(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateOptionRequest>()

        val option = useCase.createOption(body) { optionGroupUseCase.getOptionGroup(body.optionGroupId) }
        val response = OptionResponse.from(option)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getOption(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val option = useCase.getOption(id)
        val response = OptionResponse.from(option)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateOption(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateOptionRequest>()

        useCase.updateOption(id, body)
        return ok().buildAndAwait()
    }

    suspend fun changeOptionGroup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<ChangeOptionGroupIdRequest>()

        useCase.changeOptionGroup(id) { optionGroupUseCase.getOptionGroup(body.optionGroupId) }
        return ok().buildAndAwait()
    }

    suspend fun deleteOption(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        useCase.deleteOption(id)
        return ok().buildAndAwait()
    }

}