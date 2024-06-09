package hyuuny.fooddelivery.domain.cart

import CreateCartItemOptionCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("cart_item_options")
class CartItemOption(
    id: Long? = null,
    val cartItemId: Long,
    val optionId: Long,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

    companion object {
        fun handle(command: CreateCartItemOptionCommand): CartItemOption {
            return CartItemOption(
                cartItemId = command.cartItemId,
                optionId = command.optionId,
                createdAt = command.createdAt,
            )
        }
    }

}