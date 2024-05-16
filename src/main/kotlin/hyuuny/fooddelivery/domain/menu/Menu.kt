package hyuuny.fooddelivery.domain.menu

import ChangeMenuStatusCommand
import CreateMenuCommand
import UpdateMenuCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("menus")
class Menu(
    id: Long? = null,
    name: String,
    price: Price,
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
        fun handle(command: CreateMenuCommand): Menu {
            return Menu(
                name = command.name,
                price = Price(command.price),
                status = MenuStatus.ON_SALE,
                popularity = command.popularity,
                imageUrl = command.imageUrl,
                description = command.description,
                createdAt = command.createdAt,
                updatedAt = command.updatedAt
            )
        }
    }

    fun handle(command: UpdateMenuCommand) {
        name = command.name
        price = Price(command.price)
        popularity = command.popularity
        imageUrl = command.imageUrl
        description = command.description
        updatedAt = command.updatedAt
    }

    fun handle(command: ChangeMenuStatusCommand) {
        status = command.status
        updatedAt = command.updatedAt
    }

}

enum class MenuStatus(val title: String) {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    STOP_SALE("판매 중지")
}