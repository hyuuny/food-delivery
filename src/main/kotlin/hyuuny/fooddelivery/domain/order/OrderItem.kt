package hyuuny.fooddelivery.domain.order

import CreateOrderItemCommand
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

    companion object {
        fun handle(command: CreateOrderItemCommand): OrderItem = OrderItem(
            orderId = command.orderId,
            menuId = command.menuId,
            menuName = command.menuName,
            menuPrice = command.menuPrice,
            quantity = command.quantity,
            createdAt = command.createdAt,
        )
    }

    fun toMenuNameBySize(itemSize: Int): String = if (itemSize > 1) "$menuName 외 ${itemSize - 1}개"
    else "$menuName ${quantity}개"

}
