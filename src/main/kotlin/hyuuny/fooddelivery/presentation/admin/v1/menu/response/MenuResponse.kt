import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.menu.MenuStatus
import java.time.LocalDateTime


data class MenuResponse(
    val id: Long,
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val status: MenuStatus,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    constructor(entity: Menu) : this(
        id = entity.id!!,
        menuGroupId = entity.menuGroupId,
        name = entity.name,
        price = entity.price,
        status = entity.status,
        popularity = entity.popularity,
        imageUrl = entity.imageUrl,
        description = entity.description,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )
}

data class MenuResponses(
    val id: Long,
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val status: MenuStatus,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    constructor(entity: Menu) : this(
        id = entity.id!!,
        menuGroupId = entity.menuGroupId,
        name = entity.name,
        price = entity.price,
        status = entity.status,
        popularity = entity.popularity,
        imageUrl = entity.imageUrl,
        description = entity.description,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )
}