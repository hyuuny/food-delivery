package hyuuny.fooddelivery.stores.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class StoreAdminApiRouter {

    @Bean
    fun storeAdminApi(handler: StoreHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/stores".nest {
                POST("", handler::createStore)
                GET("/{id}", handler::getStore)
                GET("", handler::getStores)
                PUT("/{id}", handler::updateStore)
                DELETE("/{id}", handler::deleteStore)
            }
        }
    }

}
