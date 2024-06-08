package hyuuny.fooddelivery.domain.cart

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("cart_item_options")
class CartItemOption(
    id: Long? = null,
    val cartItemId: Long,
    val optionId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

}