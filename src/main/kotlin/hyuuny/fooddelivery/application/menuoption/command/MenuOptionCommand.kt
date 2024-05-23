import java.time.LocalDateTime

data class CreateMenuOptionCommand(
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class UpdateMenuOptionCommand(
    val name: String,
    val price: Long,
    val updatedAt: LocalDateTime,
)