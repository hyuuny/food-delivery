package hyuuny.fooddelivery.application.cart

import AddItemToCartRequest
import CreateCartCommand
import CreateCartItemCommand
import CreateCartItemOptionCommand
import UpdateCartItemOptionsRequest
import UpdateCartItemQuantityCommand
import UpdateCartItemQuantityRequest
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
        val cart = repository.findByUserId(userId) ?: insertCart(userId, now)

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
        return cart
    }

    @Transactional
    suspend fun getOrInsertCart(userId: Long): Cart = repository.findByUserId(userId)
        ?: insertCart(userId, LocalDateTime.now())

    suspend fun getCart(id: Long): Cart = findCartByIdOrThrow(id)

    @Transactional
    suspend fun updateCartItemQuantity(id: Long, cartItemId: Long, request: UpdateCartItemQuantityRequest) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 장바구니를 찾을 수 없습니다.")
        if (request.quantity <= 0) throw IllegalArgumentException("수량은 0보다 커야합니다.")

        val now = LocalDateTime.now()
        val cartItem = findCartItemByCartItemIdAndCartIdOrThrow(cartItemId, id)
        cartItem.handle(
            UpdateCartItemQuantityCommand(
                quantity = request.quantity,
                updatedAt = now,
            )
        )
        cartItemRepository.update(cartItem)
    }

    @Transactional
    suspend fun updateCartItemOptions(id: Long, cartItemId: Long, request: UpdateCartItemOptionsRequest) {
        if (request.optionIds.isEmpty()) throw IllegalArgumentException("품목 옵션은 필수값입니다.")
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 장바구니를 찾을 수 없습니다.")

        val now = LocalDateTime.now()
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
    }

    @Transactional
    suspend fun deleteCartItem(id: Long, cartItemId: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 장바구니를 찾을 수 없습니다.")

        val cartItem = findCartItemByCartItemIdAndCartIdOrThrow(cartItemId, id)
        cartItemOptionRepository.deleteAllByCartItemId(cartItem.id!!)
        cartItemRepository.delete(cartItem.id!!)
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