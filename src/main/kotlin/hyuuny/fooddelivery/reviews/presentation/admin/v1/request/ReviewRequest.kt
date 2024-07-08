import java.time.LocalDate

data class AdminReviewSearchCondition(
    val id: Long?,
    val userId: Long?,
    val userName: String?,
    val userNickname: String?,
    val storeId: Long?,
    val storeName: String?,
    val orderId: Long?,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)
