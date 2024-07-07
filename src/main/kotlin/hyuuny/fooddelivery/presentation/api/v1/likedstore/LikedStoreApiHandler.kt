package hyuuny.fooddelivery.presentation.api.v1.likedstore

import LikeOrCancelRequest
import hyuuny.fooddelivery.application.likedstore.LikedStoreUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class LikedStoreApiHandler(
    private val useCase: LikedStoreUseCase,
    private val userUseCase: UserUseCase,
    private val storeUseCase: StoreUseCase,
    private val responseMapper: LikedStoreResponseMapper,
) {

    suspend fun likeOrCancel(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<LikeOrCancelRequest>()

        useCase.likeOrCancel(
            request = body,
            getUser = { userUseCase.getUser(body.userId) },
            getStore = { storeUseCase.getStore(body.storeId) }
        )
        return ok().buildAndAwait()
    }

    suspend fun getAllLikedStores(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()

        val likedStores = useCase.getAllByUserId(userId).sortedByDescending { it.id }
        val responses = responseMapper.mapToLikedStoreResponses(likedStores)
        return ok().bodyValueAndAwait(responses)
    }

}
