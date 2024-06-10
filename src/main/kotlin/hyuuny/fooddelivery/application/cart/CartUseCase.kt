package hyuuny.fooddelivery.application.cart

import AddItemToCartRequest
import CreateCartCommand
import CreateCartItemCommand
import CreateCartItemOptionCommand
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

    private suspend fun insertCart(userId: Long, now: LocalDateTime) = repository.insert(
        Cart.handle(
            CreateCartCommand(userId = userId, createdAt = now, updatedAt = now)
        )
    )

}