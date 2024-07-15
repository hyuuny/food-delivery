package hyuuny.fooddelivery.coupons.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class CouponAdminRouter {

    @Bean
    fun couponAdminApi(handler: CouponHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/coupons".nest {
                GET("", handler::getCoupons)
                GET("/{id}", handler::getCoupon)
                POST("", handler::createCoupon)
            }
        }
    }

}
