package hyuuny.fooddelivery.coupons.presentation.admin.v1.request

import hyuuny.fooddelivery.common.constant.CouponType
import java.time.LocalDateTime

data class CreateCouponRequest(
    val code: String,
    val type: CouponType,
    val categoryId: Long?,
    val storeId: Long?,
    val name: String,
    val discountAmount: Long,
    val minimumOrderAmount: Long,
    val description: String,
    val issueStartDate: LocalDateTime,
    val issueEndDate: LocalDateTime,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
)

data class AdminCouponSearchCondition(
    val id: Long?,
    val code: String?,
    val type: CouponType?,
    val name: String?,
)
