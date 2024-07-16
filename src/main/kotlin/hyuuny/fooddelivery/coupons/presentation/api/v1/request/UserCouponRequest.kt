package hyuuny.fooddelivery.coupons.presentation.api.v1.request

data class IssueUserCouponRequest(
    val userId: Long,
    val couponId: Long,
)
