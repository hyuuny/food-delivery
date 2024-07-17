package hyuuny.fooddelivery.coupons.presentation.api.v1.response

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
