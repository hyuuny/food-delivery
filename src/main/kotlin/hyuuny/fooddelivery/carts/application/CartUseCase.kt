package hyuuny.fooddelivery.carts.application

import hyuuny.fooddelivery.carts.application.command.*
import hyuuny.fooddelivery.carts.domain.Cart
import hyuuny.fooddelivery.carts.domain.CartItem
import hyuuny.fooddelivery.carts.domain.CartItemOption
import hyuuny.fooddelivery.carts.infrastructure.CartItemOptionRepository
import hyuuny.fooddelivery.carts.infrastructure.CartItemRepository
import hyuuny.fooddelivery.carts.infrastructure.CartRepository
import hyuuny.fooddelivery.carts.presentation.api.v1.request.AddItemToCartRequest
import hyuuny.fooddelivery.carts.presentation.api.v1.request.UpdateCartItemOptionsRequest
import hyuuny.fooddelivery.carts.presentation.api.v1.request.UpdateCartItemQuantityRequest
import hyuuny.fooddelivery.stores.domain.Store
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class CartUseCase(
    private val repository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val cartItemOptionRepository: CartItemOptionRepository,
) {

    @Transactional
    suspend fun addItemToCart(
        userId: Long,
        request: AddItemToCartRequest,
        getStore: suspend () -> Store,
    ): Cart {
        if (request.item.optionIds.isEmpty()) throw IllegalArgumentException("품목 옵션은 필수값입니다.")

        val now = LocalDateTime.now()
        val store = getStore()
        val findCart = findCartByUserId(userId)
        val cart = findCart?.also {
            if (it.storeId != null && it.storeId != store.id) throw IllegalArgumentException("장바구니에는 같은 매장의 메뉴만 담을 수 있습니다.")
        } ?: insertCart(userId, store, now)

        val cartItem = cartItemRepository.insert(
            CartItem.handle(
                CreateCartItemCommand(
                    cartId = cart.id!!,
                    menuId = request.item.menuId,
                    quantity = request.item.quantity,
                    createdAt = now,
                    updatedAt = now,
                )
            )
        )

        request.item.optionIds.map { optionId ->
            CartItemOption.handle(
                CreateCartItemOptionCommand(
                    cartItemId = cartItem.id!!,
                    optionId = optionId,
                    createdAt = now,
                )
            )
        }.also { cartItemOptionRepository.insertAll(it) }

        if (findCart != null) updateCartUpdatedAt(cart, now)
        return cart
    }

    @Transactional
    suspend fun getOrInsertCart(userId: Long): Cart = findCartByUserId(userId) ?: insertCart(userId)

    @Transactional
    suspend fun updateCartItemQuantity(id: Long, cartItemId: Long, request: UpdateCartItemQuantityRequest) {
        if (request.quantity <= 0) throw IllegalArgumentException("수량은 0보다 커야합니다.")

        val now = LocalDateTime.now()
        val cart = findCartByIdOrThrow(id)
        val cartItem = findCartItemByCartItemIdAndCartIdOrThrow(cartItemId, id)
        cartItem.handle(
            UpdateCartItemQuantityCommand(
                quantity = request.quantity,
                updatedAt = now,
            )
        )
        cartItemRepository.update(cartItem)
        updateCartUpdatedAt(cart, now)
    }

    @Transactional
    suspend fun updateCartItemOptions(id: Long, cartItemId: Long, request: UpdateCartItemOptionsRequest) {
        if (request.optionIds.isEmpty()) throw IllegalArgumentException("품목 옵션은 필수값입니다.")

        val now = LocalDateTime.now()
        val cart = findCartByIdOrThrow(id)
        val cartItem = findCartItemByCartItemIdAndCartIdOrThrow(cartItemId, id)
        cartItemOptionRepository.deleteAllByCartItemId(cartItemId)
        request.optionIds.map {
            CartItemOption.handle(
                CreateCartItemOptionCommand(
                    cartItemId = cartItem.id!!,
                    optionId = it,
                    createdAt = now,
                )
            )
        }.also { cartItemOptionRepository.insertAll(it) }

        updateCartItemUpdatedAt(cartItem, now)
        updateCartUpdatedAt(cart, now)
    }

    @Transactional
    suspend fun deleteCartItem(id: Long, cartItemId: Long) {
        val cart = findCartByIdOrThrow(id)
        val cartItem = findCartItemByCartItemIdAndCartIdOrThrow(cartItemId, id)
        cartItemOptionRepository.deleteAllByCartItemId(cartItem.id!!)
        cartItemRepository.delete(cartItem.id!!)
        updateCartStoreIdAndDeliveryFee(cart)
    }

    @Transactional
    suspend fun clearCart(id: Long) {
        val cart = findCartByIdOrThrow(id)
        val cartItems = cartItemRepository.findAllByCartId(id)
        cartItemOptionRepository.deleteAllByCartItemIdIn(cartItems.mapNotNull { it.id })
        cartItemRepository.deleteAllByCartId(id)
        updateCartStoreIdAndDeliveryFee(cart)
    }

    suspend fun existsCartByUserIdAndStoreId(userId: Long, storeId: Long): Boolean =
        findCartByUserId(userId)?.let {
            it.storeId == storeId
        } ?: false

    private suspend fun updateCartStoreIdAndDeliveryFee(cart: Cart) {
        cart.clear()
        repository.updateStoreIdAndDeliveryFee(cart)
    }

    private suspend fun updateCartItemUpdatedAt(cartItem: CartItem, updatedAt: LocalDateTime) {
        cartItem.handle(UpdateCartItemUpdatedCommand(updatedAt = updatedAt))
        cartItemRepository.updateUpdatedAt(cartItem)
    }

    private suspend fun updateCartUpdatedAt(cart: Cart, updatedAt: LocalDateTime) {
        cart.handle(UpdateCartUpdatedAtCommand(updatedAt = updatedAt))
        repository.update(cart)
    }

    private suspend fun findCartByIdOrThrow(id: Long) = repository.findById(id)
        ?: throw NoSuchElementException("${id}번 장바구니를 찾을 수 없습니다.")

    private suspend fun findCartItemByCartItemIdAndCartIdOrThrow(cartItemId: Long, cartId: Long) =
        cartItemRepository.findByIdAndCartId(cartItemId, cartId)
            ?: throw NoSuchElementException("${cartId}번 장바구니의 ${cartItemId}번 품목을 찾을 수 없습니다.")

    private suspend fun insertCart(userId: Long, store: Store? = null, now: LocalDateTime = LocalDateTime.now()) =
        repository.insert(
            Cart.handle(
                CreateCartCommand(
                    userId = userId,
                    storeId = store?.id,
                    deliveryFee = store?.deliveryFee ?: 0,
                    createdAt = now,
                    updatedAt = now
                )
            )
        )

    private suspend fun findCartByUserId(userId: Long) = repository.findByUserId(userId)

}
