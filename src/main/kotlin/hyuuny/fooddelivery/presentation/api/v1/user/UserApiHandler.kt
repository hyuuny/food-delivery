package hyuuny.fooddelivery.presentation.api.v1.user

import ChangeUserNameRequest
import ChangeUserNicknameRequest
import SignUpUserRequest
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.presentation.api.v1.user.response.UserResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class UserApiHandler(
    private val useCase: UserUseCase,
) {

    suspend fun signUp(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<SignUpUserRequest>()
        val user = useCase.signUp(body)
        val response = UserResponse.from(user)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getUser(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val user = useCase.getUser(id)
        val response = UserResponse.from(user)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun changeName(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<ChangeUserNameRequest>()
        useCase.changeName(id, body)
        return ok().buildAndAwait()
    }

    suspend fun changeNickname(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<ChangeUserNicknameRequest>()
        useCase.changeNickname(id, body)
        return ok().buildAndAwait()
    }

}
