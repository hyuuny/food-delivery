package hyuuny.fooddelivery.reviewcomments.presentation.admin.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ReviewCommentAdminApiRouter {

    @Bean
    fun reviewCommentApi(handler: ReviewCommentHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/admin/v1/review-comments".nest {
                POST("", handler::createReviewComment)
                GET("", handler::getReviewComments)
                GET("/{id}", handler::getReviewComment)
                PATCH("/{id}/change-content", handler::changeContent)
                DELETE("/{id}", handler::deleteReviewComment)
            }
        }
    }

}
