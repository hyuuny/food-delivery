package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.UserCoupon
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class UserCouponRepositoryImpl(
    private val dao: UserCouponDao,
    private val template: R2dbcEntityTemplate,
) : UserCouponRepository {

    override suspend fun insert(userCoupon: UserCoupon): UserCoupon = dao.save(userCoupon)

    override suspend fun findByUserIdAndCouponId(userId: Long, couponId: Long): UserCoupon? =
        dao.findByUserIdAndCouponId(userId, couponId)

    override suspend fun findAllByUserId(userId: Long): List<UserCoupon> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByUserIdAndCouponIdIn(userId: Long, couponIds: List<Long>): List<UserCoupon> =
        dao.findAllByUserIdAndCouponIdIn(userId, couponIds)
}
