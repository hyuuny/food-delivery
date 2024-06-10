package hyuuny.fooddelivery.presentation.api.v1.cart

import AddItemToCartRequest
import hyuuny.fooddelivery.application.cart.CartItemOptionUseCase
import hyuuny.fooddelivery.application.cart.CartItemUseCase
import hyuuny.fooddelivery.application.cart.CartUseCase
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.server.ResponseStatusException

@Component
class CartApiHandler(
    private val useCase: CartUseCase,
    private val cartItemUseCase: CartItemUseCase,
    private val cartItemOptionUseCase: CartItemOptionUseCase,
    private val menuUseCase: MenuUseCase,
    private val optionUseCase: OptionUseCase,
) {

    suspend fun addItemToCart(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val body = request.awaitBody<AddItemToCartRequest>()

        if (body.item.optionIds.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션은 필수값입니다.")

        val cart = useCase.addItemToCart(userId, body)
        return coroutineScope {
            val cartItems = async { cartItemUseCase.getAllByCartId(cart.id!!) }.await()
            val menuMap = menuUseCase.getAllByIds(cartItems.map { it.menuId }).associateBy { it.id }

            val cartItemOptions =
                async { cartItemOptionUseCase.getAllByCartItemIds(cartItems.mapNotNull { it.id }) }.await()
            val optionMap = optionUseCase.getAllByIds(cartItemOptions.map { it.optionId }).associateBy { it.id }
            val cartItemOptionGroup = cartItemOptions.groupBy { it.cartItemId }

            val cartItemResponses = cartItems.mapNotNull { cartItem ->
                val menu = menuMap[cartItem.menuId] ?: return@mapNotNull null
                val optionsOfCartItem = cartItemOptionGroup[cartItem.id] ?: return@mapNotNull null

                val cartItemOptionResponse = optionsOfCartItem.mapNotNull optionMap@{ cartItemOption ->
                    val option = optionMap[cartItemOption.optionId] ?: return@optionMap null
                    CartItemOptionResponse.from(cartItemOption, option)
                }

                val itemTotalPrice = (menu.price + cartItemOptionResponse.sumOf { it.price }) * cartItem.quantity
                CartItemResponse.from(cartItem, menu, cartItemOptionResponse, itemTotalPrice)
            }

            val totalPrice = cartItemResponses.sumOf { it.price }
            val response = CartResponse.from(cart, cartItemResponses, totalPrice)
            ok().bodyValueAndAwait(response)
        }
    }

    suspend fun getCart(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()

        val cart = useCase.getOrInsertCart(userId)
        return coroutineScope {
            val cartItems = async { cartItemUseCase.getAllByCartId(cart.id!!) }.await()
            val menuMap = menuUseCase.getAllByIds(cartItems.map { it.menuId }).associateBy { it.id }

            val cartItemOptions =
                async { cartItemOptionUseCase.getAllByCartItemIds(cartItems.mapNotNull { it.id }) }.await()
            val optionMap = optionUseCase.getAllByIds(cartItemOptions.map { it.optionId }).associateBy { it.id }
            val cartItemOptionGroup = cartItemOptions.groupBy { it.cartItemId }

            val cartItemResponses = cartItems.mapNotNull { cartItem ->
                val menu = menuMap[cartItem.menuId] ?: return@mapNotNull null
                val optionsOfCartItem = cartItemOptionGroup[cartItem.id] ?: return@mapNotNull null

                val cartItemOptionResponse = optionsOfCartItem.mapNotNull optionMap@{ cartItemOption ->
                    val option = optionMap[cartItemOption.optionId] ?: return@optionMap null
                    CartItemOptionResponse.from(cartItemOption, option)
                }

                val itemTotalPrice = (menu.price + cartItemOptionResponse.sumOf { it.price }) * cartItem.quantity
                CartItemResponse.from(cartItem, menu, cartItemOptionResponse, itemTotalPrice)
            }

            val totalPrice = cartItemResponses.sumOf { it.price }
            val response = CartResponse.from(cart, cartItemResponses, totalPrice)
            ok().bodyValueAndAwait(response)
        }
    }

}
