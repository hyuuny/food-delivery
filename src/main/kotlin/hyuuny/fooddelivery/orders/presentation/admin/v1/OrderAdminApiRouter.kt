package hyuuny.fooddelivery.orders.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class OrderAdminApiRouter {

    @Bean
    fun orderAdminApi(handler: OrderHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/orders".nest {
                GET("", handler::getOrders)
                GET("/{id}", handler::getOrder)
                PATCH("/{id}/change-order-status", handler::changeOrderStatus)
            }
        }
    }

}
