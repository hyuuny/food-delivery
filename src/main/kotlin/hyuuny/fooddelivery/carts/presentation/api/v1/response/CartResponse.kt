package hyuuny.fooddelivery.carts.presentation.api.v1.response

import hyuuny.fooddelivery.carts.domain.Cart
import hyuuny.fooddelivery.carts.domain.CartItem
import hyuuny.fooddelivery.carts.domain.CartItemOption
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.options.domain.Option

data class CartResponse(
    val id: Long,
    val userId: Long,
    val storeId: Long?,
    val items: List<CartItemResponse>,
    val totalPrice: Long,
    val deliveryFee: Long,
) {
    companion object {
        fun from(entity: Cart, cartItemResponses: List<CartItemResponse>, totalPrice: Long): CartResponse {
            return CartResponse(
                id = entity.id!!,
                userId = entity.userId,
                storeId = entity.storeId,
                items = cartItemResponses,
                totalPrice = totalPrice,
                deliveryFee = entity.deliveryFee,
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
    val itemWithOptionsPrice: Long,
) {
    companion object {
        fun from(
            entity: CartItem,
            menu: Menu,
            itemOptions: List<CartItemOptionResponse>,
            itemWithOptionsPrice: Long
        ): CartItemResponse {
            return CartItemResponse(
                id = entity.id!!,
                cartId = entity.cartId,
                menuId = entity.menuId,
                menuName = menu.name,
                imageUrl = menu.imageUrl,
                quantity = entity.quantity,
                price = menu.price,
                options = itemOptions,
                itemWithOptionsPrice = itemWithOptionsPrice,
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