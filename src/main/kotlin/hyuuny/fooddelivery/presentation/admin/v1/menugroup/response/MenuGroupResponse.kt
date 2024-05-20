package hyuuny.fooddelivery.presentation.admin.v1.menugroup.response

import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import java.time.LocalDateTime

data class MenuGroupResponse(
    val id: Long,
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    constructor(entity: MenuGroup) : this(
        id = entity.id!!,
        menuId = entity.menuId,
        name = entity.name,
        required = entity.required,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )
}

data class MenuGroupResponses(
    val id: Long,
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val createdAt: LocalDateTime,
) {
    constructor(entity: MenuGroup) : this(
        id = entity.id!!,
        menuId = entity.menuId,
        name = entity.name,
        required = entity.required,
        createdAt = entity.createdAt,
    )
}
