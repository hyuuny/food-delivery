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
            GET("/{userId}/issuable", handler::getIssuableCoupons)
        }

        "/api/v1/users/{userId}/coupons".nest {
            GET("", handler::getOwnedCoupons)
            GET("/available", handler::getAvailableCoupons)
            GET("/{id}", handler::getCoupon)
        }
    }

}
