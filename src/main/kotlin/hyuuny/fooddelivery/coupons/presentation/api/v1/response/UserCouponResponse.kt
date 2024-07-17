package hyuuny.fooddelivery.coupons.presentation.api.v1.response

import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import java.time.LocalDateTime

data class UserCouponResponse(
    val id: Long,
    val userId: Long,
    val couponId: Long,
    val used: Boolean,
    val usedDate: LocalDateTime?,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val issuedDate: LocalDateTime,
) {
    companion object {
        fun from(entity: UserCoupon): UserCouponResponse = UserCouponResponse(
            id = entity.id!!,
            userId = entity.userId,
            couponId = entity.couponId,
            used = entity.used,
            usedDate = entity.usedDate,
            validFrom = entity.validFrom,
            validTo = entity.validTo,
            issuedDate = entity.issuedDate,
        )
    }
}

data class UserWithCouponResponse(
    val userId: Long,
    val couponId: Long,
    val code: String,
    val type: CouponType,
    val categoryId: Long?,
    val storeId: Long?,
    val name: String,
    val discountAmount: Long,
    val minimumOrderAmount: Long,
    val description: String,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val used: Boolean,
    val usedDate: LocalDateTime?,
) {
    companion object {
        fun from(entity: UserCoupon, coupon: Coupon): UserWithCouponResponse = UserWithCouponResponse(
            userId = entity.userId,
            couponId = entity.couponId,
            code = coupon.code,
            type = coupon.type,
            categoryId = coupon.categoryId,
            storeId = coupon.storeId,
            name = coupon.name,
            discountAmount = coupon.discountAmount,
            minimumOrderAmount = coupon.minimumOrderAmount,
            description = coupon.description,
            validFrom = entity.validFrom,
            validTo = entity.validTo,
            used = entity.used,
            usedDate = entity.usedDate
        )
    }
}

data class IssuableCouponResponses(
    val couponId: Long,
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
    val issued: Boolean,
) {
    companion object {
        fun from(coupon: Coupon, issued: Boolean): IssuableCouponResponses = IssuableCouponResponses(
            couponId = coupon.id!!,
            code = coupon.code,
            type = coupon.type,
            categoryId = coupon.categoryId,
            storeId = coupon.storeId,
            name = coupon.name,
            discountAmount = coupon.discountAmount,
            minimumOrderAmount = coupon.minimumOrderAmount,
            description = coupon.description,
            issueStartDate = coupon.issueStartDate,
            issueEndDate = coupon.issueEndDate,
            validFrom = coupon.validFrom,
            validTo = coupon.validTo,
            issued = issued,
        )
    }
}

data class OwnedUserCouponResponses(
    val couponId: Long,
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
    val available: Boolean,
) {
    companion object {
        fun from(entity: UserCoupon, coupon: Coupon): OwnedUserCouponResponses = OwnedUserCouponResponses(
            couponId = entity.couponId,
            code = coupon.code,
            type = coupon.type,
            categoryId = coupon.categoryId,
            storeId = coupon.storeId,
            name = coupon.name,
            discountAmount = coupon.discountAmount,
            minimumOrderAmount = coupon.minimumOrderAmount,
            description = coupon.description,
            issueStartDate = coupon.issueStartDate,
            issueEndDate = coupon.issueEndDate,
            validFrom = entity.validFrom,
            validTo = entity.validTo,
            available = entity.isAvailable(),
        )
    }
}

data class AvailableCouponResponses(
    val couponId: Long,
    val userId: Long,
    val code: String,
    val type: CouponType,
    val categoryId: Long?,
    val storeId: Long?,
    val name: String,
    val discountAmount: Long,
    val minimumOrderAmount: Long,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val isCouponValidForOrder: Boolean,
) {
    companion object {
        fun from(entity: UserCoupon, coupon: Coupon, isCouponValidForOrder: Boolean): AvailableCouponResponses =
            AvailableCouponResponses(
                couponId = entity.couponId,
                userId = entity.userId,
                code = coupon.code,
                type = coupon.type,
                categoryId = coupon.categoryId,
                storeId = coupon.storeId,
                name = coupon.name,
                discountAmount = coupon.discountAmount,
                minimumOrderAmount = coupon.minimumOrderAmount,
                validFrom = entity.validFrom,
                validTo = entity.validTo,
                isCouponValidForOrder = isCouponValidForOrder,
            )
    }
}
