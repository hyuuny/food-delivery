import java.time.LocalDateTime

data class CreateMenuGroupCommand(
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)