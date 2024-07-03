package hyuuny.fooddelivery.presentation.api.v1.cart

import AddItemToCartRequest
import UpdateCartItemOptionsRequest
import UpdateCartItemQuantityRequest
import hyuuny.fooddelivery.application.cart.CartUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class CartApiHandler(
    private val useCase: CartUseCase,
    private val storeUseCase: StoreUseCase,
    private val responseMapper: CartResponseMapper,
) {

    suspend fun addItemToCart(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val body = request.awaitBody<AddItemToCartRequest>()

        val cart = useCase.addItemToCart(
            userId = userId,
            getStore = { storeUseCase.getStore(body.storeId) },
            request = body
        )
        val response = responseMapper.mapToCartResponse(cart)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getCart(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()

        val cart = useCase.getOrInsertCart(userId)
        val response = responseMapper.mapToCartResponse(cart)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateCartItemQuantity(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val cartId = request.pathVariable("cartId").toLong()
        val cartItemId = request.pathVariable("cartItemId").toLong()
        val body = request.awaitBody<UpdateCartItemQuantityRequest>()

        useCase.updateCartItemQuantity(cartId, cartItemId, body)
        val cart = useCase.getOrInsertCart(userId)
        val response = responseMapper.mapToCartResponse(cart)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun updateCartItemOptions(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val cartId = request.pathVariable("cartId").toLong()
        val cartItemId = request.pathVariable("cartItemId").toLong()
        val body = request.awaitBody<UpdateCartItemOptionsRequest>()

        useCase.updateCartItemOptions(cartId, cartItemId, body)
        val cart = useCase.getOrInsertCart(userId)
        val response = responseMapper.mapToCartResponse(cart)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun deleteCartItem(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val cartId = request.pathVariable("cartId").toLong()
        val cartItemId = request.pathVariable("cartItemId").toLong()

        useCase.deleteCartItem(cartId, cartItemId)
        val cart = useCase.getOrInsertCart(userId)
        val response = responseMapper.mapToCartResponse(cart)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun existsCartByUserIdAndStoreId(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val storeId = request.pathVariable("storeId").toLong()

        val response = useCase.existsCartByUserIdAndStoreId(userId, storeId)
        return ok().bodyValueAndAwait(mapOf("exists" to response))
    }

}
