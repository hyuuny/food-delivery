package hyuuny.fooddelivery.presentation.admin.v1.menuoption.response

import hyuuny.fooddelivery.domain.menuoption.MenuOption
import java.time.LocalDateTime

data class MenuOptionResponse(
    val id: Long,
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    constructor(entity: MenuOption) : this(
        id = entity.id!!,
        menuGroupId = entity.menuGroupId,
        name = entity.name,
        price = entity.price,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
}

data class MenuOptionResponses(
    val id: Long,
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val createdAt: LocalDateTime,
){
    constructor(entity: MenuOption) : this(
        id = entity.id!!,
        menuGroupId = entity.menuGroupId,
        name = entity.name,
        price = entity.price,
        createdAt = entity.createdAt,
    )
}