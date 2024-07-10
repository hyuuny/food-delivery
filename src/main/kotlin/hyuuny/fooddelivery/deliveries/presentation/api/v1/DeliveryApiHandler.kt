package hyuuny.fooddelivery.deliveries.presentation.api.v1

import AcceptDeliveryRequest
import CancelDeliveryRequest
import DeliveredDeliveryRequest
import PickupDeliveryRequest
import hyuuny.fooddelivery.deliveries.application.DeliveryUseCase
import hyuuny.fooddelivery.deliveries.presentation.api.v1.response.DeliveryResponse
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.users.application.UserUseCase
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class DeliveryApiHandler(
    private val useCase: DeliveryUseCase,
    private val orderUseCase: OrderUseCase,
    private val userUseCase: UserUseCase,
) {

    suspend fun acceptDelivery(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<AcceptDeliveryRequest>()

        val delivery = useCase.acceptDelivery(
            request = body,
            getOrder = { orderUseCase.getOrder(body.orderId) },
            getRider = { userUseCase.getUser(body.riderId) },
        )
        val response = DeliveryResponse.from(delivery)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun cancel(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<CancelDeliveryRequest>()

        useCase.cancel(
            id = id,
            getOrder = { orderUseCase.getOrder(body.orderId) },
            getRider = { userUseCase.getUser(body.riderId) },
        )
        return ok().buildAndAwait()
    }

    suspend fun pickup(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<PickupDeliveryRequest>()

        useCase.pickup(
            id = id,
            getOrder = { orderUseCase.getOrder(body.orderId) },
            getRider = { userUseCase.getUser(body.riderId) },
        )
        return ok().buildAndAwait()
    }

    suspend fun delivered(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<DeliveredDeliveryRequest>()

        useCase.delivered(
            id = id,
            getOrder = { orderUseCase.getOrder(body.orderId) },
            getRider = { userUseCase.getUser(body.riderId) },
        )
        return ok().buildAndAwait()
    }

}
