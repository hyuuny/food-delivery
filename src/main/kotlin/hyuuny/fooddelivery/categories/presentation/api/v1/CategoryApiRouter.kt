package hyuuny.fooddelivery.categories.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CategoryApiRouter {

    @Bean
    fun categoryApi(handler: CategoryApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/categories".nest {
                GET("/delivery-type/{deliveryType}", handler::getVisibleCategoriesByDeliveryTypeOrderByPriority)
            }
        }
    }

}
