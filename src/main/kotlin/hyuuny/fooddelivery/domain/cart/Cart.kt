package hyuuny.fooddelivery.domain.cart

import CreateCartCommand
import UpdateCartUpdatedAtCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("carts")
class Cart(
    id: Long? = null,
    val userId: Long,
    storeId: Long? = null,
    deliveryFee: Long = 0,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var storeId = storeId
        private set
    var deliveryFee = deliveryFee
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateCartCommand): Cart = Cart(
            userId = command.userId,
            createdAt = command.createdAt,
            updatedAt = command.updatedAt,
        )
    }

    fun handle(command: UpdateCartUpdatedAtCommand) {
        this.updatedAt = command.updatedAt
    }

}
