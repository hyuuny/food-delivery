package hyuuny.fooddelivery.coupons.application

import hyuuny.fooddelivery.common.log.Log
import hyuuny.fooddelivery.coupons.application.command.IssueUserCouponCommand
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.infrastructure.UserCouponRepository
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.ApiCouponSearchCondition
import hyuuny.fooddelivery.users.domain.User
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class UserCouponUseCase(
    private val repository: UserCouponRepository,
) {

    companion object : Log

    suspend fun getUserCouponByApiCondition(
        searchCondition: ApiCouponSearchCondition,
        pageable: Pageable,
    ): PageImpl<UserCoupon> = repository.findAllUserCoupons(searchCondition, pageable)

    @Transactional
    suspend fun issueCoupon(
        getUser: suspend () -> User,
        getCoupon: suspend () -> Coupon,
    ): UserCoupon {
        val now = LocalDateTime.now()
        val user = getUser()
        val coupon = getCoupon()

        if (repository.existsByUserIdAndCouponId(user.id!!, coupon.id!!)) throw IllegalStateException("이미 발급된 쿠폰입니다.")
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

    suspend fun getAllByUserIdAndCouponIds(userId: Long, couponIds: List<Long>) =
        repository.findAllByUserIdAndCouponIdIn(userId, couponIds)

    suspend fun getAllAvailableUserCoupon(userId: Long): List<UserCoupon> {
        val now = LocalDateTime.now()
        return repository.findAllByUserIdAndUsedFalse(userId, now)
    }

    suspend fun getUserCoupon(
        couponId: Long,
        getUser: suspend () -> User,
    ): UserCoupon {
        val user = getUser()
        return findUserCouponByUserIdAndCouponIdOrThrow(user.id!!, couponId)
    }

    private suspend fun findUserCouponByUserIdAndCouponIdOrThrow(userId: Long, couponId: Long) =
        repository.findByUserIdAndCouponId(userId, couponId)
            ?: throw NoSuchElementException("${userId}번 회원의 ${couponId}번 쿠폰을 찾을 수 없습니다.")

}
