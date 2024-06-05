package hyuuny.fooddelivery.presentation.admin.v1

import hyuuny.fooddelivery.presentation.admin.v1.category.CategoryHandler
import hyuuny.fooddelivery.presentation.admin.v1.menu.MenuHandler
import hyuuny.fooddelivery.presentation.admin.v1.menugroup.MenuGroupHandler
import hyuuny.fooddelivery.presentation.admin.v1.option.OptionHandler
import hyuuny.fooddelivery.presentation.admin.v1.optiongroup.OptionGroupHandler
import hyuuny.fooddelivery.presentation.admin.v1.store.StoreHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AdminRoutes {

    @Bean
    fun menuAdminApi(handler: MenuHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/menus".nest {
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
    fun optionGroupAdminApi(handler: OptionGroupHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/option-groups".nest {
                GET("", handler::getOptionGroups)
                GET("/{id}", handler::getOptionGroup)
            }

            "/admin/v1/menus/{menuId}/option-groups".nest {
                POST("", handler::createOptionGroup)
                PUT("/{id}", handler::updateOptionGroup)
                PATCH("/re-order", handler::reOrderOptionGroup)
                DELETE("/{id}", handler::deleteOptionGroup)
            }
        }
    }

    @Bean
    fun optionAdminApi(handler: OptionHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/options".nest {
                GET("", handler::getOptions)
                GET("/{id}", handler::getOption)
            }

            "/admin/v1/option-groups/{optionGroupId}/options".nest {
                POST("", handler::createOption)
                PUT("/{id}", handler::updateOption)
                DELETE("/{id}", handler::deleteOption)
            }
        }
    }

    @Bean
    fun menuGroupAdminApi(handler: MenuGroupHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/menu-groups".nest {
                GET("", handler::getMenuGroups)
                GET("/{id}", handler::getMenuGroup)
            }

            "/admin/v1/stores/{storeId}/menu-groups".nest {
                POST("", handler::createMenuGroup)
                PUT("/{id}", handler::updateMenuGroup)
                PATCH("/re-order", handler::reOrderMenuGroup)
                DELETE("/{id}", handler::deleteMenuGroup)
            }
        }
    }

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

    @Bean
    fun categoryAdminApi(handler: CategoryHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/categories".nest {
                POST("", handler::createCategory)
            }
        }
    }

}