package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.UserCoupon

interface UserCouponRepository {

    suspend fun insert(userCoupon: UserCoupon): UserCoupon

    suspend fun findByUserIdAndCouponId(userId: Long, couponId: Long): UserCoupon?

    suspend fun findAllByUserId(userId: Long): List<UserCoupon>

    suspend fun findAllByUserIdAndCouponIdIn(userId: Long, couponIds: List<Long>): List<UserCoupon>

}
