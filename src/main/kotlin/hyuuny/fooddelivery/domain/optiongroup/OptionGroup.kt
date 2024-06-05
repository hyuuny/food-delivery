package hyuuny.fooddelivery.domain.optiongroup

import CreateOptionGroupCommand
import ReOrderOptionGroupCommand
import UpdateOptionGroupCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "option_groups")
class OptionGroup(
    id: Long? = null,
    val menuId: Long,
    name: String,
    required: Boolean = false,
    priority: Int,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var name = name
        private set
    var required = required
        private set
    var priority = priority
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateOptionGroupCommand): OptionGroup {
            if (command.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

            return OptionGroup(
                menuId = command.menuId,
                name = command.name,
                required = command.required,
                priority = command.priority,
                createdAt = command.createdAt,
                updatedAt = command.updatedAt
            )
        }
    }

    fun handle(command: UpdateOptionGroupCommand) {
        if (command.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

        this.name = command.name
        this.required = command.required
        this.updatedAt = command.updatedAt
    }

    fun handle(command: ReOrderOptionGroupCommand) {
        this.priority = command.priority
        this.updatedAt = command.updatedAt
    }

}