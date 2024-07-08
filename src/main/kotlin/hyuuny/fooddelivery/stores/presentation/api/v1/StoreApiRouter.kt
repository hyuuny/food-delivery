package hyuuny.fooddelivery.stores.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class StoreApiRouter {

    @Bean
    fun storeApi(handler: StoreApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/stores".nest {
                GET("/{id}", handler::getStore)
                GET("", handler::getStores)
            }
        }
    }

}
