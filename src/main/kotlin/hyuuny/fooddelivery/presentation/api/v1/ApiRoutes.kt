package hyuuny.fooddelivery.presentation.api.v1

import hyuuny.fooddelivery.presentation.api.v1.cart.CartApiHandler
import hyuuny.fooddelivery.presentation.api.v1.category.CategoryApiHandler
import hyuuny.fooddelivery.presentation.api.v1.menu.MenuApiHandler
import hyuuny.fooddelivery.presentation.api.v1.order.OrderApiHandler
import hyuuny.fooddelivery.presentation.api.v1.review.ReviewApiHandler
import hyuuny.fooddelivery.presentation.api.v1.store.StoreApiHandler
import hyuuny.fooddelivery.presentation.api.v1.user.UserApiHandler
import hyuuny.fooddelivery.presentation.api.v1.useraddress.UserAddressApiHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ApiRoutes {

    @Bean
    fun menuApi(handler: MenuApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/menus".nest {
                GET("/{id}", handler::getMenu)
            }
        }
    }

    @Bean
    fun storeApi(handler: StoreApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/stores".nest {
                GET("/{id}", handler::getStore)
                GET("", handler::getStores)
            }
        }
    }

    @Bean
    fun categoryApi(handler: CategoryApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/categories".nest {
                GET("/delivery-type/{deliveryType}", handler::getVisibleCategoriesByDeliveryTypeOrderByPriority)
            }
        }
    }

    @Bean
    fun cartApi(handler: CartApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users/{userId}/carts".nest {
                POST("", handler::addItemToCart)
                GET("", handler::getCart)
                PUT("/{cartId}/cart-items/{cartItemId}", handler::updateCartItemQuantity)
                PUT("/{cartId}/cart-items/{cartItemId}/options", handler::updateCartItemOptions)
                DELETE("/{cartId}/cart-items/{cartItemId}", handler::deleteCartItem)
            }
        }
    }

    @Bean
    fun userApi(handler: UserApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users".nest {
                POST("/sign-up", handler::signUp)
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

    @Bean
    fun orderApi(handler: OrderApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users/{userId}/carts/{cartId}/orders".nest {
                POST("", handler::createOrder)
            }

            "/api/v1/users/{userId}/orders".nest {
                GET("/{id}", handler::getOrder)
                GET("", handler::getOrders)
                PATCH("/{id}/cancel", handler::cancelOrder)
                PATCH("/{id}/refund", handler::refundOrder)
            }
        }
    }

    @Bean
    fun reviewApi(handler: ReviewApiHandler): RouterFunction<ServerResponse> {
        return coRouter {
            "/api/v1/users/{userId}/reviews".nest {
                POST("", handler::createReview)
            }
        }
    }

}
