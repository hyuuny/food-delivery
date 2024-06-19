package hyuuny.fooddelivery.domain.order

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("order_items")
class OrderItem(
    id: Long? = null,
    val orderId: Long,
    val menuId: Long,
    val menuName: String,
    val menuPrice: Long,
    val quantity: Int,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

}
