package hyuuny.fooddelivery.deliveries.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class DeliveryAdminRouter {

    @Bean
    fun deliveryAdminApi(handler: DeliveryHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/deliveries".nest {
                GET("", handler::getDeliveries)
                GET("/{id}", handler::getDelivery)
                PATCH("/{id}/change-deliver-status", handler::changeDeliveryStatus)
            }
        }
    }

}
