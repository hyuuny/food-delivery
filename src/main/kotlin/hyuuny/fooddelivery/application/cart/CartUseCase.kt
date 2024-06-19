package hyuuny.fooddelivery.application.cart

import AddItemToCartRequest
import CreateCartCommand
import CreateCartItemCommand
import CreateCartItemOptionCommand
import UpdateCartItemOptionsRequest
import UpdateCartItemQuantityCommand
import UpdateCartItemQuantityRequest
import UpdateCartItemUpdatedCommand
import UpdateCartUpdatedAtCommand
import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.infrastructure.cart.CartItemOptionRepository
import hyuuny.fooddelivery.infrastructure.cart.CartItemRepository
import hyuuny.fooddelivery.infrastructure.cart.CartRepository
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
    suspend fun addItemToCart(userId: Long, request: AddItemToCartRequest): Cart {
        if (request.item.optionIds.isEmpty()) throw IllegalArgumentException("품목 옵션은 필수값입니다.")

        val now = LocalDateTime.now()
        val existingCart = repository.findByUserId(userId)
        val cart = existingCart ?: insertCart(userId, now)

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

        if (existingCart != null) updateCartUpdatedAt(cart, now)
        return cart
    }

    @Transactional
    suspend fun getOrInsertCart(userId: Long): Cart = repository.findByUserId(userId)
        ?: insertCart(userId, LocalDateTime.now())

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

        cart.handle(UpdateCartUpdatedAtCommand(updatedAt = now))
        repository.update(cart)
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
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 장바구니를 찾을 수 없습니다.")

        val cartItem = findCartItemByCartItemIdAndCartIdOrThrow(cartItemId, id)
        cartItemOptionRepository.deleteAllByCartItemId(cartItem.id!!)
        cartItemRepository.delete(cartItem.id!!)
    }

    @Transactional
    suspend fun clearCart(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 장바구니를 찾을 수 없습니다.")

        val cartItems = cartItemRepository.findAllByCartId(id)
        cartItemOptionRepository.deleteAllByCartItemIdIn(cartItems.mapNotNull { it.id })
        cartItemRepository.deleteAllByCartId(id)
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

    private suspend fun insertCart(userId: Long, now: LocalDateTime) = repository.insert(
        Cart.handle(
            CreateCartCommand(userId = userId, createdAt = now, updatedAt = now)
        )
    )

}