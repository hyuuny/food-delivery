package hyuuny.fooddelivery.domain.cart

import CreateCartItemCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("cart_items")
class CartItem(
    id: Long? = null,
    val cartId: Long,
    val menuId: Long,
    quantity: Int,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var quantity = quantity
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateCartItemCommand): CartItem {
            return CartItem(
                cartId = command.cartId,
                menuId = command.menuId,
                quantity = command.quantity,
                createdAt = command.createdAt,
                updatedAt = command.updatedAt
            )
        }
    }

}