package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.UserCoupon
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserCouponDao : CoroutineCrudRepository<UserCoupon, Long> {

    suspend fun findByUserIdAndCouponId(userId: Long, couponId: Long): UserCoupon?

    suspend fun findAllByUserId(userId: Long): List<UserCoupon>

    suspend fun findAllByUserIdAndCouponIdIn(userId: Long, couponIds: List<Long>): List<UserCoupon>

}
