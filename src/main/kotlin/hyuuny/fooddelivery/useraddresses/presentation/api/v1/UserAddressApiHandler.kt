package hyuuny.fooddelivery.useraddresses.presentation.api.v1

import hyuuny.fooddelivery.useraddresses.application.UserAddressUseCase
import hyuuny.fooddelivery.useraddresses.domain.UserAddress
import hyuuny.fooddelivery.useraddresses.presentation.api.v1.request.CreateUserAddressRequest
import hyuuny.fooddelivery.useraddresses.presentation.api.v1.request.UpdateUserAddressRequest
import hyuuny.fooddelivery.useraddresses.presentation.api.v1.response.UserAddressResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class UserAddressApiHandler(
    private val useCase: UserAddressUseCase,
) {

    suspend fun createUserAddress(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val body = request.awaitBody<CreateUserAddressRequest>()

        val userAddress = useCase.createUserAddress(userId, body)
        val response = UserAddressResponse.from(userAddress)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getAllUserAddresses(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()

        val userAddresses = useCase.getAllUserAddress(userId)
            .sortedWith(compareByDescending<UserAddress> { it.selected }.thenBy { it.id })
        val response = userAddresses.map { UserAddressResponse.from(it) }
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getUserAddress(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val userAddress = useCase.getUserAddress(id)
        val response = UserAddressResponse.from(userAddress)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateUserAddress(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<UpdateUserAddressRequest>()

        useCase.updateUserAddress(id, body)
        return ok().buildAndAwait()
    }

    suspend fun changeUserAddressSelectedToTrue(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val id = request.pathVariable("id").toLong()

        useCase.changeUserAddressSelectedToTrue(userId, id)
        return ok().buildAndAwait()
    }

    suspend fun deleteUserAddress(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        useCase.deleteUserAddress(id)
        return ok().buildAndAwait()
    }

}
