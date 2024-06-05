package hyuuny.fooddelivery.presentation.admin.v1.option

import AdminOptionSearchCondition
import CreateOptionRequest
import UpdateOptionRequest
import extractCursorAndCount
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.presentation.admin.v1.option.response.OptionResponse
import hyuuny.fooddelivery.presentation.admin.v1.option.response.OptionResponses
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException
import parseSort

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
        val responses = SimplePage(page.content.map { OptionResponses(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun createOption(request: ServerRequest): ServerResponse {
        val optionGroupId = request.pathVariable("optionGroupId").toLong()
        val body = request.awaitBody<CreateOptionRequest>()

        if (!optionGroupUseCase.existsById(optionGroupId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 옵션그룹입니다."
        )

        val option = useCase.createOption(body)
        return ok().bodyValueAndAwait(option)
    }

    suspend fun getOption(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val option = useCase.getOption(id)
        val response = OptionResponse(option)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateOption(request: ServerRequest): ServerResponse {
        val optionGroupId = request.pathVariable("optionGroupId").toLong()
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateOptionRequest>()

        if (!optionGroupUseCase.existsById(optionGroupId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 옵션입니다."
        )

        useCase.updateOption(id, body)
        return ok().buildAndAwait()
    }

    suspend fun deleteOption(request: ServerRequest): ServerResponse {
        val optionGroupId = request.pathVariable("optionGroupId").toLong()
        val id = request.pathVariable("id").toLong()

        if (!optionGroupUseCase.existsById(optionGroupId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 옵션그룹입니다."
        )

        useCase.deleteOption(id)
        return ok().buildAndAwait()
    }

}