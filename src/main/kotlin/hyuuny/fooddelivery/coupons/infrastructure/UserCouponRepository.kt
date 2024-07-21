package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.ApiCouponSearchCondition
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface UserCouponRepository {

    suspend fun insert(userCoupon: UserCoupon): UserCoupon

    suspend fun findByUserIdAndCouponId(userId: Long, couponId: Long): UserCoupon?

    suspend fun findAllUserCoupons(searchCondition: ApiCouponSearchCondition, pageable: Pageable): PageImpl<UserCoupon>

    suspend fun findAllByUserIdAndCouponIdIn(userId: Long, couponIds: List<Long>): List<UserCoupon>

    suspend fun findAllByUserIdAndUsedFalse(userId: Long, now: LocalDateTime): List<UserCoupon>

    suspend fun existsByUserIdAndCouponId(userId: Long, couponId: Long): Boolean

    suspend fun updateUsedAndUsedDate(userCoupon: UserCoupon)

}
