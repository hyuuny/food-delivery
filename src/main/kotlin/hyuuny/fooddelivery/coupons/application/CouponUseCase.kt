package hyuuny.fooddelivery.coupons.application

import CreateCouponCommand
import hyuuny.fooddelivery.common.log.Log
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.infrastructure.CouponRepository
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.AdminCouponSearchCondition
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.CreateCouponRequest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class CouponUseCase(
    private val repository: CouponRepository,
) {

    companion object : Log

    suspend fun getCouponsByAdminCondition(
        searchCondition: AdminCouponSearchCondition,
        pageable: Pageable
    ): PageImpl<Coupon> = repository.findAllCoupons(searchCondition, pageable)

    suspend fun getCoupon(id: Long): Coupon = findCouponByIdOrThrow(id)

    @Transactional
    suspend fun createCoupon(request: CreateCouponRequest): Coupon {
        val now = LocalDateTime.now()

        if (repository.findByCode(request.code) != null) throw IllegalArgumentException("이미 사용중인 쿠폰 코드입니다. code: ${request.code}")
        CouponVerifier.verify(request, now)

        val coupon = Coupon.handle(
            CreateCouponCommand(
                code = request.code,
                type = request.type,
                name = request.name,
                discountAmount = request.discountAmount,
                minimumOrderAmount = request.minimumOrderAmount,
                description = request.description,
                issueStartDate = request.issueStartDate,
                issueEndDate = request.issueEndDate,
                validFrom = request.validFrom,
                validTo = request.validTo,
                createdAt = now,
            )
        )

        log.info("Create Coupon Code: ${coupon.code}")
        return repository.insert(coupon)
    }

    suspend fun getAllIssuableCoupon(now: LocalDateTime): List<Coupon> {
        return repository.findAllByIssueStartDateLessThanEqualAndIssueEndDateGreaterThanEqual(now)
    }

    suspend fun getAllAvailableCoupon(now: LocalDateTime): List<Coupon> {
        return repository.findAllByValidFromLessThanEqualAndValidToGreaterThanEqual(now)
    }

    private suspend fun findCouponByIdOrThrow(id: Long) =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 쿠폰을 찾을 수 없습니다.")

}
