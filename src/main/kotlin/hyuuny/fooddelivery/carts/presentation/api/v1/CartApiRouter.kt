package hyuuny.fooddelivery.carts.presentation.api.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CartApiRouter {

    @Bean
    fun cartApi(handler: CartApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users/{userId}/carts".nest {
                POST("", handler::addItemToCart)
                GET("", handler::getCart)
                PUT("/{cartId}/cart-items/{cartItemId}", handler::updateCartItemQuantity)
                PUT("/{cartId}/cart-items/{cartItemId}/options", handler::updateCartItemOptions)
                DELETE("/{cartId}/cart-items/{cartItemId}", handler::deleteCartItem)
                GET("/stores/{storeId}/exists", handler::existsCartByUserIdAndStoreId)
            }
        }
    }
}
