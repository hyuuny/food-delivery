package hyuuny.fooddelivery.orders.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class OrderApiRouter {

    @Bean
    fun orderApi(handler: OrderApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users/{userId}/carts/{cartId}/orders".nest {
                POST("", handler::createOrder)
            }

            "/api/v1/users/{userId}/orders".nest {
                GET("/{id}", handler::getOrder)
                GET("", handler::getOrders)
                PATCH("/{id}/cancel", handler::cancelOrder)
                PATCH("/{id}/refund", handler::refundOrder)
            }
        }
    }

}
