package hyuuny.fooddelivery.domain.menugroup

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

}