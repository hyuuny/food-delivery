package hyuuny.fooddelivery.coupons.application

import hyuuny.fooddelivery.common.log.Log
import hyuuny.fooddelivery.coupons.application.command.IssueUserCouponCommand
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.infrastructure.UserCouponRepository
import hyuuny.fooddelivery.users.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class UserCouponUseCase(
    private val repository: UserCouponRepository,
) {

    companion object : Log

    @Transactional
    suspend fun issueCoupon(
        getUser: suspend () -> User,
        getCoupon: suspend () -> Coupon,
    ): UserCoupon {
        val now = LocalDateTime.now()
        val user = getUser()
        val coupon = getCoupon()

        if (findUserCouponByUserIdAndCouponIdOrNull(user, coupon) != null) throw IllegalStateException("이미 발급된 쿠폰입니다.")
        if (now !in coupon.getIssuancePeriod()) throw IllegalStateException("쿠폰 발급 기간이 아닙니다.")

        val userCoupon = UserCoupon.handle(
            IssueUserCouponCommand(
                userId = user.id!!,
                couponId = coupon.id!!,
                issuedDate = now,
                validFrom = coupon.validFrom,
                validTo = coupon.validTo,
            )
        )
        log.info("issued coupon id: ${coupon.id}, code: ${coupon.code} user id: ${user.id}")
        return repository.insert(userCoupon)
    }

    private suspend fun findUserCouponByUserIdAndCouponIdOrNull(user: User, coupon: Coupon) =
        repository.findByUserIdAndCouponId(user.id!!, coupon.id!!)

}
