package hyuuny.fooddelivery.orders.application

import CreateOrderRequest
import hyuuny.fooddelivery.carts.domain.Cart
import hyuuny.fooddelivery.carts.domain.CartItem
import hyuuny.fooddelivery.carts.domain.CartItemOption
import hyuuny.fooddelivery.carts.infrastructure.CartItemOptionRepository
import hyuuny.fooddelivery.carts.infrastructure.CartItemRepository
import hyuuny.fooddelivery.carts.infrastructure.CartRepository
import hyuuny.fooddelivery.menus.infrastructure.MenuRepository
import hyuuny.fooddelivery.options.infrastructure.OptionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class OrderCartVerifier(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val cartItemOptionRepository: CartItemOptionRepository,
    private val menuRepository: MenuRepository,
    private val optionRepository: OptionRepository,
) {

    suspend fun verify(cartId: Long, request: CreateOrderRequest) {
        coroutineScope {
            val cartDeferred = async { cartRepository.findById(cartId) }
            val cartItemsDeferred = async { cartItemRepository.findAllByCartId(cartId) }

            val cart = cartDeferred.await() ?: throw NoSuchElementException("${cartId}번 장바구니를 찾을 수 없습니다.")
            val cartItems = cartItemsDeferred.await()
            val cartItemOptions = cartItemOptionRepository.findAllByCartItemIdIn(cartItems.mapNotNull { it.id })

            val menuDeferred = async { menuRepository.findAllByIdIn(cartItems.map { it.menuId }) }
            val optionDeferred = async { optionRepository.findAllByIdIn(cartItemOptions.map { it.optionId }) }

            verifyMenuIdsInCart(cartItems, request)
            verifyOptionIdsInCart(cartItemOptions, request)
            verifyDeliveryFee(cart, request)

            val menuTotalPrice = menuDeferred.await().sumOf { it.price }
            val options = optionDeferred.await()
            val optionTotalPrice = options.sumOf { it.price }
            val cartTotalPrice = menuTotalPrice + optionTotalPrice

            if (cartTotalPrice != request.totalPrice) {
                throw IllegalStateException("장바구니 금액과 주문 금액이 일치하지 않습니다.")
            }
        }
    }

    private fun verifyMenuIdsInCart(cartItems: List<CartItem>, request: CreateOrderRequest) {
        val cartItemMenuIdSet = cartItems.map { it.menuId }.toSet()
        val orderItemMenuIdSet = request.orderItems.map { it.menuId }.toSet()

        if (cartItemMenuIdSet != orderItemMenuIdSet) throw IllegalStateException("장바구니에 담긴 메뉴와 일치하지 않습니다.")
    }

    private fun verifyOptionIdsInCart(cartItemOptions: List<CartItemOption>, request: CreateOrderRequest) {
        val cartItemOptionIdSet = cartItemOptions.map { it.optionId }.toSet()
        val orderItemOptionIdSet = request.orderItems.flatMap { it.optionIds }.toSet()

        if (cartItemOptionIdSet != orderItemOptionIdSet) throw IllegalStateException("메뉴의 옵션이 일치하지 않습니다.")
    }

    private fun verifyDeliveryFee(cart: Cart, request: CreateOrderRequest) {
        if (cart.deliveryFee != request.deliveryFee) throw IllegalStateException("배달비가 일치하지 않습니다.")
    }

}
