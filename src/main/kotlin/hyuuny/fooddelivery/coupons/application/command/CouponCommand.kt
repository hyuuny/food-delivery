import hyuuny.fooddelivery.common.constant.CouponType
import java.time.LocalDateTime

data class CreateCouponCommand(
    val code: String,
    val type: CouponType,
    val name: String,
    val discountAmount: Long,
    val minimumOrderAmount: Long,
    val description: String,
    val issueStartDate: LocalDateTime,
    val issueEndDate: LocalDateTime,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val createdAt: LocalDateTime,
)
