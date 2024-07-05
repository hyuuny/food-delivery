import java.time.LocalDateTime

data class CreateOptionCommand(
    val optionGroupId: Long,
    val name: String,
    val price: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class UpdateOptionCommand(
    val name: String,
    val price: Long,
    val updatedAt: LocalDateTime,
)

data class ChangeOptionGroupIdCommand(
    val optionGroupId: Long,
    val updatedAt: LocalDateTime,
)