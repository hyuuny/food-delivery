package hyuuny.fooddelivery.users.presentation.admin.v1

import AdminUserSearchCondition
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.convertToLocalDate
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.presentation.admin.v1.response.UserResponse
import hyuuny.fooddelivery.users.presentation.admin.v1.response.UserResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class UserHandler(
    private val useCase: UserUseCase,
) {

    suspend fun getUsers(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val nickname = request.queryParamOrNull("nickname")?.takeIf { it.isNotBlank() }
        val email = request.queryParamOrNull("email")?.takeIf { it.isNotBlank() }
        val phoneNumber = request.queryParamOrNull("phoneNumber")?.takeIf { it.isNotBlank() }
        val fromDate = request.queryParamOrNull("fromDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val toDate = request.queryParamOrNull("toDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val searchCondition = AdminUserSearchCondition(id, name, nickname, email, phoneNumber, fromDate, toDate)

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getUsersByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { UserResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getUser(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val user = useCase.getUser(id)
        val response = UserResponse.from(user)
        return ok().bodyValueAndAwait(response)
    }

}
