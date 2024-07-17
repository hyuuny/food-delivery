package hyuuny.fooddelivery.coupons.application

import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.CreateCouponRequest
import java.time.LocalDateTime

object CouponVerifier {

    fun verify(request: CreateCouponRequest, now: LocalDateTime = LocalDateTime.now()) {
        with(request) {
            if (code.length <= 3) throw IllegalArgumentException("쿠폰 코드는 4자 이상이여야 합니다. code: $code")
            if (type.isCategory() && categoryId == null) throw IllegalArgumentException("카테고리 쿠폰은 카테고리 아이디가 필수 값입니다.")
            if (type.isStore() && storeId == null) throw IllegalArgumentException("매장 쿠폰은 매장 아이디가 필수 값입니다.")
            if (discountAmount < 1000) throw IllegalArgumentException("할인 금액은 1000원 이상이여야 합니다. discountAmount: $discountAmount")
            if (issueStartDate.isBefore(now)) throw IllegalArgumentException("쿠폰 발급 시작일은 현재 시간 이후여야 합니다.")
            if (issueStartDate.isAfter(issueEndDate)) throw IllegalArgumentException("쿠폰 발급 시작일은 종료일 이전이여야 합니다.")
            if (validFrom.isBefore(now)) throw IllegalArgumentException("쿠폰 사용 시작일은 현재 시간 이후여야 합니다.")
            if (validFrom.isAfter(validTo)) throw IllegalArgumentException("쿠폰 사용 시작일은 종료일 이전이여야 합니다.")
        }
    }

}
