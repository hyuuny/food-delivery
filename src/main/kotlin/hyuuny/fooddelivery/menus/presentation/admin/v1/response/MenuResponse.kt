import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.menus.domain.Menu
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
    companion object {
        fun from(entity: Menu) = MenuResponse(
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
    companion object {
        fun from(entity: Menu) = MenuResponses(
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
}