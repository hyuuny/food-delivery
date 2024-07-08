import java.time.LocalDateTime

data class CreateMenuGroupCommand(
    val storeId: Long,
    val name: String,
    val priority: Int,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class UpdateMenuGroupCommand(
    val name: String,
    val description: String?,
    val updatedAt: LocalDateTime,
)

data class ReOrderMenuGroupCommand(
    val priority: Int,
    val updatedAt: LocalDateTime,
)
