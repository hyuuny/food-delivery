package hyuuny.fooddelivery.likedstores.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class LikedStoreApiRouter {

    @Bean
    fun likedStoreApi(handler: LikedStoreApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/liked-stores".nest {
                POST("", handler::likeOrCancel)
            }

            "/api/v1/users/{userId}/liked-stores".nest {
                GET("", handler::getAllLikedStores)
            }
        }
    }

}
