package hyuuny.fooddelivery.domain.cart

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

}