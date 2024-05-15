package hyuuny.fooddelivery.domain.menu

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("menus")
class Menu(
    id: Long? = null,
    val name: String,
    val price: Price,
    val status: MenuStatus = MenuStatus.ON_SALE,
    val popularity: Boolean = false,
    val imageUrl: String? = null,
    val description: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

}

enum class MenuStatus(val title: String) {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    STOP_SALE("판매 중지")
}