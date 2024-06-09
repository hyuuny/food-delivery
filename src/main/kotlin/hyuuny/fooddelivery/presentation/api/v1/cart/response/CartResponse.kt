package hyuuny.fooddelivery.presentation.api.v1.cart.response

import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option

data class CartResponse(
    val id: Long,
    val userId: Long,
    val items: List<CartItemResponse>,
    val totalPrice: Long,
) {
    companion object {
        fun from(entity: Cart, cartItemResponses: List<CartItemResponse>, totalPrice: Long): CartResponse {
            return CartResponse(
                id = entity.id!!,
                userId = entity.userId,
                items = cartItemResponses,
                totalPrice = totalPrice
            )
        }
    }
}

data class CartItemResponse(
    val id: Long,
    val cartId: Long,
    val menuId: Long,
    val menuName: String,
    val imageUrl: String?,
    val quantity: Int,
    val price: Long,
    val options: List<CartItemOptionResponse>,
) {
    companion object {
        fun from(
            entity: CartItem,
            menu: Menu,
            itemOptions: List<CartItemOptionResponse>,
            itemTotalPrice: Long
        ): CartItemResponse {
            return CartItemResponse(
                id = entity.id!!,
                cartId = entity.cartId,
                menuId = entity.menuId,
                menuName = menu.name,
                imageUrl = menu.imageUrl,
                quantity = entity.quantity,
                price = itemTotalPrice,
                options = itemOptions
            )
        }
    }
}

data class CartItemOptionResponse(
    val id: Long,
    val cartItemId: Long,
    val optionId: Long,
    val optionName: String,
    val price: Long,
) {
    companion object {
        fun from(entity: CartItemOption, option: Option): CartItemOptionResponse {
            return CartItemOptionResponse(
                id = entity.id!!,
                cartItemId = entity.cartItemId,
                optionId = entity.optionId,
                optionName = option.name,
                price = option.price
            )
        }
    }
}
