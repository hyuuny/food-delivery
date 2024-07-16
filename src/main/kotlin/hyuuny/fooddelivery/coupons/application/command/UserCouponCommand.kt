package hyuuny.fooddelivery.coupons.application.command

import java.time.LocalDateTime

data class IssueUserCouponCommand(
    val userId: Long,
    val couponId: Long,
    val issuedDate: LocalDateTime,
)
