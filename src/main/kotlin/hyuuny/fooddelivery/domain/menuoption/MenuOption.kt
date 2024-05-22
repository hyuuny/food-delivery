package hyuuny.fooddelivery.domain.menuoption

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("menu_option")
class MenuOption(
    id: Long? = null,
    val menuGroupId: Long,
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

}