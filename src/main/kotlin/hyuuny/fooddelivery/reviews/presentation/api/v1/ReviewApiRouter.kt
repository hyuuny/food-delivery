package hyuuny.fooddelivery.reviews.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ReviewApiRouter {

    @Bean
    fun reviewApi(handler: ReviewApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users/{userId}/reviews".nest {
                POST("", handler::createReview)
                DELETE("/{id}", handler::deleteReview)
            }

            "/api/v1/reviews".nest {
                GET("", handler::getReviews)
            }
        }
    }

}
