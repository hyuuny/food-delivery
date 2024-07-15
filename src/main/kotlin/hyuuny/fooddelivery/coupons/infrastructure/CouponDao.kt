package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.Coupon
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CouponDao : CoroutineCrudRepository<Coupon, Long> {

    suspend fun findByCode(code: String): Coupon?

}
