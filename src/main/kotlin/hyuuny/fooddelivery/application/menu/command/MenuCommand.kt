import hyuuny.fooddelivery.common.constant.MenuStatus
import java.time.LocalDateTime

data class CreateMenuCommand(
    val name: String,
    val menuGroupId: Long,
    val price: Long,
    val status: MenuStatus,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class UpdateMenuCommand(
    val name: String,
    val price: Long,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
    val updatedAt: LocalDateTime
)

data class ChangeMenuStatusCommand(
    val status: MenuStatus,
    val updatedAt: LocalDateTime
)