package hyuuny.fooddelivery.categories.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CategoryAdminApiRouter {

    @Bean
    fun categoryAdminApi(handler: CategoryHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/categories".nest {
                POST("", handler::createCategory)
                GET("/{id}", handler::getCategory)
                GET("", handler::getCategories)
                GET("/delivery-type/{deliveryType}", handler::getVisibleCategoriesByDeliveryTypeOrderByPriority)
                PUT("/{id}", handler::updateCategory)
                PATCH("/delivery-type/{deliveryType}/re-order", handler::reOrderCategories)
                DELETE("/{id}", handler::deleteCategory)
            }
        }
    }

}
