package hyuuny.fooddelivery.domain.menugroup

import CreateMenuGroupCommand
import ReOrderMenuGroupCommand
import UpdateMenuGroupCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("menu_groups")
class MenuGroup(
    id: Long? = null,
    val storeId: Long,
    name: String,
    priority: Int,
    description: String? = null,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var name = name
        private set
    var priority = priority
        private set
    var description = description
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateMenuGroupCommand): MenuGroup = MenuGroup(
            storeId = command.storeId,
            name = command.name,
            priority = command.priority,
            description = command.description,
            createdAt = command.createdAt,
            updatedAt = command.updatedAt,
        )
    }

    fun handle(command: UpdateMenuGroupCommand) {
        this.name = command.name
        this.description = command.description
        this.updatedAt = command.updatedAt
    }

    fun handle(command: ReOrderMenuGroupCommand) {
        this.priority = command.priority
        this.updatedAt = command.updatedAt
    }
}
