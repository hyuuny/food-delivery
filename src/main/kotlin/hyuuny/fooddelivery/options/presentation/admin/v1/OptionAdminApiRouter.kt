package hyuuny.fooddelivery.options.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class OptionAdminApiRouter {

    @Bean
    fun optionAdminApi(handler: OptionHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/options".nest {
                GET("", handler::getOptions)
                GET("/{id}", handler::getOption)
                POST("", handler::createOption)
                PUT("/{id}", handler::updateOption)
                PATCH("/{id}/change-option-group", handler::changeOptionGroup)
                DELETE("/{id}", handler::deleteOption)
            }
        }
    }

}
