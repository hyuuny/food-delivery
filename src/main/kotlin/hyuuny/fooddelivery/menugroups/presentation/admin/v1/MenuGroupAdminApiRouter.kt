package hyuuny.fooddelivery.menugroups.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MenuGroupAdminApiRouter {

    @Bean
    fun menuGroupAdminApi(handler: MenuGroupHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/menu-groups".nest {
                GET("", handler::getMenuGroups)
                GET("/{id}", handler::getMenuGroup)
                POST("", handler::createMenuGroup)
                PUT("/{id}", handler::updateMenuGroup)
                PATCH("/re-order", handler::reOrderMenuGroup)
                DELETE("/{id}", handler::deleteMenuGroup)
            }
        }
    }

}
