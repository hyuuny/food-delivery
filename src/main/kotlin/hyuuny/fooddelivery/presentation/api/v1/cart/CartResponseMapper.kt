package hyuuny.fooddelivery.presentation.api.v1.cart

import hyuuny.fooddelivery.application.cart.CartItemOptionUseCase
import hyuuny.fooddelivery.application.cart.CartItemUseCase
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class CartResponseMapper(
    private val cartItemUseCase: CartItemUseCase,
    private val cartItemOptionUseCase: CartItemOptionUseCase,
    private val menuUseCase: MenuUseCase,
    private val optionUseCase: OptionUseCase
) {
    suspend fun mapToCartResponse(cart: Cart): CartResponse = coroutineScope {
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

            val itemWithOptionsPrice = (menu.price + cartItemOptionResponse.sumOf { it.price }) * cartItem.quantity
            CartItemResponse.from(cartItem, menu, cartItemOptionResponse, itemWithOptionsPrice)
        }

        val totalPrice = cartItemResponses.sumOf { it.itemWithOptionsPrice }
        CartResponse.from(cart, cartItemResponses, totalPrice)
    }
}