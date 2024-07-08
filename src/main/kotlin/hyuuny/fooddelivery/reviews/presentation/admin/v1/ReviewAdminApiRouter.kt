package hyuuny.fooddelivery.reviews.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ReviewAdminApiRouter {

    @Bean
    fun reviewAdminApi(handler: ReviewHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/reviews".nest {
                GET("", handler::getReviews)
                GET("/{id}", handler::getReview)
                DELETE("/{id}", handler::deleteReview)
            }
        }
    }

}
