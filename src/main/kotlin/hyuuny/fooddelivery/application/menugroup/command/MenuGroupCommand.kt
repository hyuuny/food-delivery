import java.time.LocalDateTime

data class CreateMenuGroupCommand(
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val priority: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class UpdateMenuGroupCommand(
    val name: String,
    val required: Boolean,
    val updatedAt: LocalDateTime,
)

data class ReOrderMenuGroupCommand(
    val priority: Int,
    val updatedAt: LocalDateTime,
)