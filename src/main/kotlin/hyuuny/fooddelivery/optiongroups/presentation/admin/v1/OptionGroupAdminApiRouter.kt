package hyuuny.fooddelivery.optiongroups.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class OptionGroupAdminApiRouter {

    @Bean
    fun optionGroupAdminApi(handler: OptionGroupHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/option-groups".nest {
                GET("", handler::getOptionGroups)
                GET("/{id}", handler::getOptionGroup)
                POST("", handler::createOptionGroup)
                PUT("/{id}", handler::updateOptionGroup)
                PATCH("/re-order", handler::reOrderOptionGroup)
                DELETE("/{id}", handler::deleteOptionGroup)
            }
        }
    }

}
