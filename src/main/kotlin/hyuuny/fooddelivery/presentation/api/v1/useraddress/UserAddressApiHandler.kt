package hyuuny.fooddelivery.presentation.api.v1.useraddress

import hyuuny.fooddelivery.application.useraddress.UserAddressUseCase
import hyuuny.fooddelivery.domain.useraddress.UserAddress
import hyuuny.fooddelivery.presentation.api.v1.useraddress.request.CreateUserAddressRequest
import hyuuny.fooddelivery.presentation.api.v1.useraddress.request.UpdateUserAddressRequest
import hyuuny.fooddelivery.presentation.api.v1.useraddress.response.UserAddressResponse
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

}
