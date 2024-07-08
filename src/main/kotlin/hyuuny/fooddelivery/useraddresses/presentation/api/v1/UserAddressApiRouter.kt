package hyuuny.fooddelivery.useraddresses.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserAddressApiRouter {

    @Bean
    fun userAddressApi(handler: UserAddressApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users/{userId}/addresses".nest {
                POST("", handler::createUserAddress)
                GET("", handler::getAllUserAddresses)
                GET("/{id}", handler::getUserAddress)
                PUT("/{id}", handler::updateUserAddress)
                PATCH("/{id}/change-selected", handler::changeUserAddressSelectedToTrue)
                DELETE("/{id}", handler::deleteUserAddress)
            }
        }
    }

}
