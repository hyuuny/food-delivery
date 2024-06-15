import java.time.LocalDate

data class AdminUserSearchCondition(
    val id: Long?,
    val name: String?,
    val nickname: String?,
    val email: String?,
    val phoneNumber: String?,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)
