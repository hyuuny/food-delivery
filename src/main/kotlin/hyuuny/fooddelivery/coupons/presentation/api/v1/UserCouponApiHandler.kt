package hyuuny.fooddelivery.coupons.presentation.api.v1

import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.application.UserCouponUseCase
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.IssueUserCouponRequest
import hyuuny.fooddelivery.coupons.presentation.api.v1.response.UserCouponResponse
import hyuuny.fooddelivery.users.application.UserUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserCouponApiHandler(
    private val useCase: UserCouponUseCase,
    private val userUseCase: UserUseCase,
    private val couponUseCase: CouponUseCase,
) {

    suspend fun issueUserCoupon(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<IssueUserCouponRequest>()

        val userCoupon = useCase.issueCoupon(
            getCoupon = { couponUseCase.getCoupon(body.couponId) },
            getUser = { userUseCase.getUser(body.userId) }
        )
        val response = UserCouponResponse.from(userCoupon)
        return ok().bodyValueAndAwait(response)
    }

}
