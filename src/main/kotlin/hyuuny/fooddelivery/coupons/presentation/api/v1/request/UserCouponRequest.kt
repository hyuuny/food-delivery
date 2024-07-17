package hyuuny.fooddelivery.coupons.presentation.api.v1.request

import java.time.LocalDateTime

data class IssueUserCouponRequest(
    val userId: Long,
    val couponId: Long,
)

data class ApiCouponSearchCondition(
    val userId: Long,
    val now: LocalDateTime,
    val used: Boolean,
)
