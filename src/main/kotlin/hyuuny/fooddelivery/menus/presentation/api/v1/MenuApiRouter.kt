package hyuuny.fooddelivery.menus.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MenuApiRouter {

    @Bean
    fun menuApi(handler: MenuApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/menus".nest {
                GET("/{id}", handler::getMenu)
            }
        }
    }

}
