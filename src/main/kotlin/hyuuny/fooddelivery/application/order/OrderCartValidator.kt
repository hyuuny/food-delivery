package hyuuny.fooddelivery.application.order

import CreateOrderRequest
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import hyuuny.fooddelivery.infrastructure.cart.CartRepository
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import hyuuny.fooddelivery.infrastructure.option.OptionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class OrderCartValidator(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val cartItemOptionRepository: CartItemOptionRepository,
    private val menuRepository: MenuRepository,
    private val optionRepository: OptionRepository,
) {

    suspend fun validate(cartId: Long, request: CreateOrderRequest) {
        coroutineScope {
            val cartDeferred = async { cartRepository.findById(cartId) }
            val cartItemsDeferred = async { cartItemRepository.findAllByCartId(cartId) }

            cartDeferred.await() ?: throw NoSuchElementException("${cartId}번 장바구니를 찾을 수 없습니다.")
            val cartItems = cartItemsDeferred.await()
            val cartItemOptions = cartItemOptionRepository.findAllByCartItemIdIn(cartItems.mapNotNull { it.id })

            val menuDeferred = async { menuRepository.findAllByIdIn(cartItems.map { it.menuId }) }
            val optionDeferred = async { optionRepository.findAllByIdIn(cartItemOptions.map { it.optionId }) }

            verifyMenuIdsInCart(cartItems, request)
            verifyOptionIdsInCart(cartItemOptions, request)

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

}

