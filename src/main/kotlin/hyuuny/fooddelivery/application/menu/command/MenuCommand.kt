import hyuuny.fooddelivery.domain.menu.MenuStatus
import java.time.LocalDateTime

data class CreateMenuCommand(
    val name: String,
    val price: Long,
    val status: MenuStatus,
    val popularity: Boolean,
    val imageUrl: String?,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)