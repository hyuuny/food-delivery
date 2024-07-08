package hyuuny.fooddelivery.menus.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MenuAdminApiRouter {

    @Bean
    fun menuAdminApi(handler: MenuHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/menus".nest {
                POST("", handler::createMenu)
                GET("", handler::getMenus)
                GET("/{id}", handler::getMenu)
                PUT("/{id}", handler::updateMeno)
                PATCH("/{id}/change-status", handler::changeMenuStatus)
                PATCH("/{id}/change-menu-group", handler::changeMenuGroup)
                DELETE("/{id}", handler::deleteMenu)
            }
        }
    }

}
