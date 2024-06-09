package hyuuny.fooddelivery.domain.cart

import CreateCartCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("carts")
class Cart(
    id: Long? = null,
    val userId: Long,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateCartCommand): Cart {
            return Cart(
                userId = command.userId,
                createdAt = command.createdAt,
                updatedAt = command.updatedAt,
            )
        }
    }
}
