import java.time.LocalDateTime

data class CreateOptionGroupCommand(
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val priority: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class UpdateOptionGroupCommand(
    val name: String,
    val required: Boolean,
    val updatedAt: LocalDateTime,
)

data class ReOrderOptionGroupCommand(
    val priority: Int,
    val updatedAt: LocalDateTime,
)