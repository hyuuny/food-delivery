package hyuuny.fooddelivery.users.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserApiRouter {

    @Bean
    fun userApi(handler: UserApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users".nest {
                POST("/sign-up/customers", handler::signUp)
                POST("/sign-up/riders", handler::signUpRider)
                GET("/{id}", handler::getUser)
                PATCH("/{id}/change-name", handler::changeName)
                PATCH("/{id}/change-nickname", handler::changeNickname)
                PATCH("/{id}/change-email", handler::changeEmail)
                PATCH("/{id}/change-phone-number", handler::changePhoneNumber)
                PATCH("/{id}/change-image-url", handler::changeImageUrl)
                DELETE("/{id}", handler::deleteUser)
            }
        }
    }

}
