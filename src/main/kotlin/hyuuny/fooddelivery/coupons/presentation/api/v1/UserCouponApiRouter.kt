package hyuuny.fooddelivery.coupons.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class UserCouponApiRouter {

    @Bean
    fun couponApi(handler: UserCouponApiHandler) = coRouter {
        "/api/v1/coupons".nest {
            POST("/issue", handler::issueUserCoupon)
        }
    }

}
