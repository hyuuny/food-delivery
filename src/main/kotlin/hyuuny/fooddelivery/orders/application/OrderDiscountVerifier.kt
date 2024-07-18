package hyuuny.fooddelivery.orders.application

import CreateOrderRequest
import hyuuny.fooddelivery.coupons.infrastructure.CouponRepository
import hyuuny.fooddelivery.coupons.infrastructure.UserCouponRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderDiscountVerifier(
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
) {

    suspend fun verifyCouponDiscount(userId: Long, request: CreateOrderRequest) = coroutineScope {
        val couponId = request.couponId!!

        val couponDeferred = async { couponRepository.findById(couponId) }
        val userCouponDeferred = async { userCouponRepository.findByUserIdAndCouponId(userId, couponId) }

        val coupon = couponDeferred.await() ?: throw NoSuchElementException("${couponId}번 쿠폰을 찾을 수 없습니다.")
        val userCoupon = userCouponDeferred.await()
            ?: throw NoSuchElementException("${userId}번 회원의 ${couponId}번 쿠폰을 찾을 수 없습니다.")

        if (userCoupon.used) throw IllegalStateException("이미 사용한 쿠폰입니다.")
        if (LocalDateTime.now() !in coupon.getIssuancePeriod()) throw IllegalStateException("유효 기간이 아닙니다.")
        if (request.orderPrice - request.couponDiscountAmount < 0) throw IllegalStateException("총 금액이 0보다 작을 수 없습니다.")
        if (coupon.discountAmount != request.couponDiscountAmount) throw IllegalStateException("쿠폰 할인 금액이 일치하지 않습니다.")
        if (coupon.isApplicableForOrder(request.categoryId, request.storeId)) {
            throw IllegalStateException("해당 주문에 사용할 수 없는 쿠폰입니다.")
        }
    }

}
