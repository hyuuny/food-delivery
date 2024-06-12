package hyuuny.fooddelivery.domain.option

import CreateOptionCommand
import UpdateOptionCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("options")
class Option(
    id: Long? = null,
    val optionGroupId: Long,
    name: String,
    price: Long = 0,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var name = name
        private set
    var price = price
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateOptionCommand): Option = Option(
            optionGroupId = command.optionGroupId,
            name = command.name,
            price = command.price,
            createdAt = command.createdAt,
            updatedAt = command.updatedAt
        )
    }

    fun handle(command: UpdateOptionCommand) {
        name = command.name
        price = command.price
        updatedAt = command.updatedAt
    }

}