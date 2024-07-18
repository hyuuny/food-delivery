package hyuuny.fooddelivery.coupons.presentation.api.v1

import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.presentation.api.v1.response.AvailableCouponResponses
import hyuuny.fooddelivery.coupons.presentation.api.v1.response.IssuableCouponResponses
import hyuuny.fooddelivery.coupons.presentation.api.v1.response.OwnedUserCouponResponses
import kotlinx.coroutines.coroutineScope
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

    suspend fun mapToIssuableCouponResponses(
        issuableCoupons: List<Coupon>,
        userCoupons: List<UserCoupon>
    ): List<IssuableCouponResponses> {
        val userCouponMap = userCoupons.associateBy { it.couponId }

        return issuableCoupons.map {
            val userCoupon = userCouponMap[it.id]
            IssuableCouponResponses.from(it, userCoupon != null)
        }
    }

    suspend fun mapToAvailableCouponResponses(
        categoryId: Long,
        storeId: Long,
        userCoupons: List<UserCoupon>
    ) = coroutineScope {
        val couponIds = userCoupons.map { it.couponId }
        val coupons = couponUseCase.getAllByIds(couponIds)
        val couponMap = coupons.associateBy { it.id }

        userCoupons.mapNotNull {
            val coupon = couponMap[it.couponId] ?: return@mapNotNull null
            val isCouponValidForOrder = coupon.isApplicableForOrder(categoryId, storeId)

            AvailableCouponResponses.from(it, coupon, isCouponValidForOrder)
        }.sortedWith(compareByDescending<AvailableCouponResponses> { it.isCouponValidForOrder }
            .thenByDescending { it.discountAmount })
    }

}
