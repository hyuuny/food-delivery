import hyuuny.fooddelivery.menugroups.domain.MenuGroup
import java.time.LocalDateTime

data class MenuGroupResponse(
    val id: Long,
    val storeId: Long,
    val name: String,
    val priority: Int,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(entity: MenuGroup): MenuGroupResponse = MenuGroupResponse(
            id = entity.id!!,
            storeId = entity.storeId,
            name = entity.name,
            priority = entity.priority,
            description = entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}

data class MenuGroupResponses(
    val id: Long,
    val storeId: Long,
    val name: String,
    val priority: Int,
    val description: String?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: MenuGroup): MenuGroupResponses = MenuGroupResponses(
            id = entity.id!!,
            storeId = entity.storeId,
            name = entity.name,
            priority = entity.priority,
            description = entity.description,
            createdAt = entity.createdAt,
        )
    }
}
