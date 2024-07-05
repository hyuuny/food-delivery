package hyuuny.fooddelivery.domain.menu

import ChangeMenuGroupCommand
import ChangeMenuStatusCommand
import CreateMenuCommand
import UpdateMenuCommand
import hyuuny.fooddelivery.common.constant.MenuStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("menus")
class Menu(
    id: Long? = null,
    menuGroupId: Long,
    name: String,
    price: Long,
    status: MenuStatus = MenuStatus.ON_SALE,
    popularity: Boolean = false,
    imageUrl: String? = null,
    description: String? = null,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var menuGroupId = menuGroupId
        private set
    var name = name
        private set
    var price = price
        private set
    var status = status
        private set
    var popularity = popularity
        private set
    var imageUrl = imageUrl
        private set
    var description = description
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateMenuCommand): Menu = Menu(
            menuGroupId = command.menuGroupId,
            name = command.name,
            price = command.price,
            status = MenuStatus.ON_SALE,
            popularity = command.popularity,
            imageUrl = command.imageUrl,
            description = command.description,
            createdAt = command.createdAt,
            updatedAt = command.updatedAt
        )
    }

    fun handle(command: UpdateMenuCommand) {
        name = command.name
        price = command.price
        popularity = command.popularity
        imageUrl = command.imageUrl
        description = command.description
        updatedAt = command.updatedAt
    }

    fun handle(command: ChangeMenuStatusCommand) {
        status = command.status
        updatedAt = command.updatedAt
    }

    fun handle(command: ChangeMenuGroupCommand) {
        menuGroupId = command.menuGroupId
        updatedAt = command.updatedAt
    }

}