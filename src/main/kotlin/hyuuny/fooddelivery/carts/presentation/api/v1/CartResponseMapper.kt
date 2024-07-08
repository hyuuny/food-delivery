package hyuuny.fooddelivery.carts.presentation.api.v1

import hyuuny.fooddelivery.carts.application.CartItemOptionUseCase
import hyuuny.fooddelivery.carts.application.CartItemUseCase
import hyuuny.fooddelivery.carts.domain.Cart
import hyuuny.fooddelivery.carts.presentation.api.v1.response.CartItemOptionResponse
import hyuuny.fooddelivery.carts.presentation.api.v1.response.CartItemResponse
import hyuuny.fooddelivery.carts.presentation.api.v1.response.CartResponse
import hyuuny.fooddelivery.menus.application.MenuUseCase
import hyuuny.fooddelivery.options.application.OptionUseCase
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
            CartItemResponse.from(
                entity = cartItem,
                menu = menu,
                itemOptions = cartItemOptionResponse,
                itemWithOptionsPrice = itemWithOptionsPrice
            )
        }.sortedByDescending { it.id }

        val totalPrice = cartItemResponses.sumOf { it.itemWithOptionsPrice }
        CartResponse.from(
            entity = cart,
            cartItemResponses = cartItemResponses,
            totalPrice = totalPrice
        )
    }
}
