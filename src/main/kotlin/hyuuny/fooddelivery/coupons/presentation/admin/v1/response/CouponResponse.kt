package hyuuny.fooddelivery.coupons.presentation.admin.v1.response

import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.domain.Coupon
import java.time.LocalDateTime

data class CouponResponse(
    val id: Long,
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
) {
    companion object {
        fun from(entity: Coupon): CouponResponse = CouponResponse(
            id = entity.id!!,
            code = entity.code,
            type = entity.type,
            name = entity.name,
            discountAmount = entity.discountAmount,
            minimumOrderAmount = entity.minimumOrderAmount,
            description = entity.description,
            issueStartDate = entity.issueStartDate,
            issueEndDate = entity.issueEndDate,
            validFrom = entity.validFrom,
            validTo = entity.validTo,
            createdAt = entity.createdAt,
        )
    }
}

data class CouponResponses(
    val id: Long,
    val code: String,
    val type: CouponType,
    val name: String,
    val discountAmount: Long,
    val issueStartDate: LocalDateTime,
    val issueEndDate: LocalDateTime,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Coupon): CouponResponses = CouponResponses(
            id = entity.id!!,
            code = entity.code,
            type = entity.type,
            name = entity.name,
            discountAmount = entity.discountAmount,
            issueStartDate = entity.issueStartDate,
            issueEndDate = entity.issueEndDate,
            validFrom = entity.validFrom,
            validTo = entity.validTo,
            createdAt = entity.createdAt,
        )
    }
}
