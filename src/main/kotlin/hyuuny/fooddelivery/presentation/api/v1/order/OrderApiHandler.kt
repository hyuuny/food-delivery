package hyuuny.fooddelivery.presentation.api.v1.order

import CreateOrderRequest
import hyuuny.fooddelivery.application.cart.CartUseCase
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.order.OrderUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class OrderApiHandler(
    private val useCase: OrderUseCase,
    private val menuUseCase: MenuUseCase,
    private val userUseCase: UserUseCase,
    private val optionUseCase: OptionUseCase,
    private val cartUseCase: CartUseCase,
    private val responseMapper: OrderResponseMapper,
) {

    suspend fun createOrder(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val cartId = request.pathVariable("cartId").toLong()
        val body = request.awaitBody<CreateOrderRequest>()

        val order = useCase.createOrder(
            cartId = cartId,
            request = body,
            getUser = { userUseCase.getUser(userId) },
            getMenus = { menuUseCase.getAllByIds(body.orderItems.map { it.menuId }) },
            getOptions = { optionUseCase.getAllByIds(body.orderItems.flatMap { it.optionIds }) },
        )
        cartUseCase.clearCart(cartId)
        val response = responseMapper.mapToOrderResponse(order)
        return ok().bodyValueAndAwait(response)
    }


}
