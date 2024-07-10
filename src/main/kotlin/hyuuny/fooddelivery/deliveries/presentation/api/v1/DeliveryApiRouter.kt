package hyuuny.fooddelivery.deliveries.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class DeliveryApiRouter {

    @Bean
    fun deliveryApi(handler: DeliveryApiHandler) = coRouter {
        "/api/v1/deliveries".nest {
            POST("/accept", handler::acceptDelivery)
            PATCH("/{id}/cancel", handler::cancel)
            PATCH("/{id}/pickup", handler::pickup)
            PATCH("/{id}/delivered", handler::delivered)
        }

        "/api/v1/users/{userId}/deliveries".nest {
            GET("", handler::getDeliveries)
        }
    }

}
