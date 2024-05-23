package hyuuny.fooddelivery.presentation.admin.v1

import hyuuny.fooddelivery.presentation.admin.v1.menu.MenuHandler
import hyuuny.fooddelivery.presentation.admin.v1.menugroup.MenuGroupHandler
import hyuuny.fooddelivery.presentation.admin.v1.menuoption.MenuOptionHandler
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

    @Bean
    fun menuGroupApi(handler: MenuGroupHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/v1/menu-groups".nest {
                GET("", handler::getMenuGroups)
                GET("/{id}", handler::getMenuGroup)
            }

            "/v1/menus/{menuId}/menu-groups".nest {
                POST("", handler::createMenuGroup)
                PUT("/{id}", handler::updateMenuGroup)
                PATCH("/re-order", handler::reOrderMenuGroup)
                DELETE("/{id}", handler::deleteMenuGroup)
            }
        }
    }

    @Bean
    fun menuOptionApi(handler: MenuOptionHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/v1/menu-options".nest {
                GET("", handler::getMenuOptions)
            }

            "/v1/menu-groups/{menuGroupId}/menu-options".nest {
                POST("", handler::createMenuOption)
                PUT("/{id}", handler::updateMenuOption)
            }
        }
    }

}