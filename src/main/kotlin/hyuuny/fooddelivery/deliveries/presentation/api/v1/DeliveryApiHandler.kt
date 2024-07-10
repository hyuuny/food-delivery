package hyuuny.fooddelivery.deliveries.presentation.api.v1

import AcceptDeliveryRequest
import ApiDeliverSearchCondition
import CancelDeliveryRequest
import DeliveredDeliveryRequest
import PickupDeliveryRequest
import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.convertToLocalDate
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.deliveries.application.DeliveryUseCase
import hyuuny.fooddelivery.deliveries.presentation.api.v1.response.DeliveryResponse
import hyuuny.fooddelivery.deliveries.presentation.api.v1.response.DeliveryResponses
import hyuuny.fooddelivery.orders.application.OrderUseCase
import hyuuny.fooddelivery.users.application.UserUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class DeliveryApiHandler(
    private val useCase: DeliveryUseCase,
    private val orderUseCase: OrderUseCase,
    private val userUseCase: UserUseCase,
    private val responseMapper: DeliveryResponseMapper,
) {

    suspend fun getDeliveries(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()
        val id = request.queryParamOrNull("id")?.toLong()
        val orderNumber = request.queryParamOrNull("orderNumber")?.takeIf { it.isNotBlank() }
        val status = request.queryParamOrNull("status")?.takeIf { it.isNotBlank() }
            ?.let { DeliveryStatus.valueOf(it.uppercase().trim()) }
        val fromDate = request.queryParamOrNull("fromDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val toDate = request.queryParamOrNull("toDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val searchCondition = ApiDeliverSearchCondition(
            id = id,
            userId = userId,
            orderNumber = orderNumber,
            status = status,
            fromDate = fromDate,
            toDate = toDate
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getAllDeliveryByApiCondition(searchCondition, pageRequest)
        val deliveryResponses = responseMapper.mapToDeliveryResponses(page.content)
        val simplePage = SimplePage(deliveryResponses, page)
        val riderUser = userUseCase.getUser(userId)
        val responses = DeliveryResponses.from(riderUser, simplePage)
        return ok().bodyValueAndAwait(responses)
    }

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
