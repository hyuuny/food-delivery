package hyuuny.fooddelivery.domain.order

import CreateOrderItemOptionCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("order_item_options")
class OrderItemOption(
    id: Long? = null,
    val orderItemId: Long,
    val optionId: Long,
    val optionName: String,
    val optionPrice: Long,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

    companion object {
        fun handle(command: CreateOrderItemOptionCommand): OrderItemOption = OrderItemOption(
            orderItemId = command.orderItemId,
            optionId = command.optionId,
            optionName = command.optionName,
            optionPrice = command.optionPrice,
            createdAt = command.createdAt,
        )
    }

}
