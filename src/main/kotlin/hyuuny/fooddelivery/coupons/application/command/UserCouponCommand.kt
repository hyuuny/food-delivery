package hyuuny.fooddelivery.coupons.application.command

import java.time.LocalDateTime

data class IssueUserCouponCommand(
    val userId: Long,
    val couponId: Long,
    val issuedDate: LocalDateTime,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
)

data class UseCouponCommand(
    val used: Boolean,
    val useDate: LocalDateTime,
)

data class CancelUseCouponCommand(
    val used: Boolean,
    val usedDate: LocalDateTime?,
)
