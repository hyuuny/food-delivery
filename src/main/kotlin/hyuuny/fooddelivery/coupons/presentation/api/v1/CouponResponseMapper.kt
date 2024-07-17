package hyuuny.fooddelivery.coupons.presentation.api.v1

import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.presentation.api.v1.response.OwnedUserCouponResponses
import org.springframework.stereotype.Component

@Component
class CouponResponseMapper(
    private val couponUseCase: CouponUseCase
) {

    suspend fun mapToOwnedUserCouponResponses(userCoupons: List<UserCoupon>): List<OwnedUserCouponResponses> {
        val couponIds = userCoupons.map { it.couponId }
        val coupons = couponUseCase.getAllByIds(couponIds)
        val couponMap = coupons.associateBy { it.id }

        return userCoupons.mapNotNull {
            val coupon = couponMap[it.couponId] ?: return@mapNotNull null
            OwnedUserCouponResponses.from(it, coupon)
        }
    }

}
