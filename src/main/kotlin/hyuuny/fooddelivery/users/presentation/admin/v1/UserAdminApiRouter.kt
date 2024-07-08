package hyuuny.fooddelivery.users.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserAdminApiRouter {

    @Bean
    fun userAdminApi(handler: UserHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/users".nest {
                GET("", handler::getUsers)
                GET("/{id}", handler::getUser)
            }
        }
    }

}
