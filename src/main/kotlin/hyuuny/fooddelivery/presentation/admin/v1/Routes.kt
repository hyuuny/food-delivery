package hyuuny.fooddelivery.presentation.admin.v1

import hyuuny.fooddelivery.presentation.admin.v1.menu.MenuHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Routes {

    @Bean
    fun menuApi(handler: MenuHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/v1/menus".nest {
                POST("", handler::createMenu)
                GET("", handler::getMenus)
                GET("/{id}", handler::getMenu)
                PUT("/{id}", handler::updateMeno)
                PATCH("/change-status/{id}", handler::changeMenuStatus)
                DELETE("/{id}", handler::deleteMenu)
            }
        }
    }

}