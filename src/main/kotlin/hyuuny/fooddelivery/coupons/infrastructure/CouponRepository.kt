package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.AdminCouponSearchCondition
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface CouponRepository {

    suspend fun insert(coupon: Coupon): Coupon

    suspend fun findById(id: Long): Coupon?

    suspend fun findByCode(code: String): Coupon?

    suspend fun findAllByIssueStartDateLessThanEqualAndIssueEndDateGreaterThanEqual(now: LocalDateTime): List<Coupon>

    suspend fun findAllCoupons(searchCondition: AdminCouponSearchCondition, pageable: Pageable): PageImpl<Coupon>

}
