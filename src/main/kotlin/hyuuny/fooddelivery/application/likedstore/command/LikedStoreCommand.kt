import java.time.LocalDateTime

data class LikeOrCancelCommand(
    val userId: Long,
    val storeId: Long,
    val createdAt: LocalDateTime,
)
