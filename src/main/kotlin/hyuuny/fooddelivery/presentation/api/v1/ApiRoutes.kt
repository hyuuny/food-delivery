package hyuuny.fooddelivery.presentation.api.v1

import hyuuny.fooddelivery.presentation.api.v1.menu.MenuApiHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ApiRoutes {

    @Bean
    fun menuApi(handler: MenuApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/menus".nest {
                GET("/{id}", handler::getMenu)
            }
        }
    }

}
