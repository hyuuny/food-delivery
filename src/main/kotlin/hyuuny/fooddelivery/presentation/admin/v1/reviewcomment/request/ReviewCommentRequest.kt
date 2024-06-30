import java.time.LocalDate

data class CreateReviewCommentRequest(
    val userId: Long,
    val reviewId: Long,
    val content: String,
)

data class AdminReviewCommentSearchCondition(
    val id: Long?,
    val userId: Long?,
    val reviewId: Long?,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)
