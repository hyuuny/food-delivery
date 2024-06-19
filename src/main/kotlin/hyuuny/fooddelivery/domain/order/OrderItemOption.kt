package hyuuny.fooddelivery.domain.order

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

}
