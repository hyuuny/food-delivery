package hyuuny.fooddelivery.domain.menugroup

import CreateMenuGroupCommand
import UpdateMenuGroupCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "menu_group")
class MenuGroup(
    id: Long? = null,
    val menuId: Long,
    name: String,
    required: Boolean = false,
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
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateMenuGroupCommand): MenuGroup {
            if (command.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

            return MenuGroup(
                menuId = command.menuId,
                name = command.name,
                required = command.required,
                createdAt = command.createdAt,
                updatedAt = command.updatedAt
            )
        }
    }

    fun handle(command: UpdateMenuGroupCommand) {
        if (command.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

        this.name = command.name
        this.required = command.required
        this.updatedAt = command.updatedAt
    }

}