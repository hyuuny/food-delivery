package hyuuny.fooddelivery.deliveries.presentation.admin.v1

import AdminDeliverySearchCondition
import ChangeDeliveryStatusRequest
import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.convertToLocalDate
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.deliveries.application.DeliveryUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class DeliveryHandler(
    private val useCase: DeliveryUseCase,
    private val responseMapper: DeliveryResponseMapper
) {

    suspend fun getDeliveries(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val riderId = request.queryParamOrNull("riderId")?.toLong()
        val riderName = request.queryParamOrNull("riderName")?.takeIf { it.isNotBlank() }
        val orderId = request.queryParamOrNull("orderId")?.toLong()
        val userName = request.queryParamOrNull("userName")?.takeIf { it.isNotBlank() }
        val orderNumber = request.queryParamOrNull("orderNumber")?.takeIf { it.isNotBlank() }
        val storeName = request.queryParamOrNull("storeName")?.takeIf { it.isNotBlank() }
        val status = request.queryParamOrNull("status")?.takeIf { it.isNotBlank() }
            ?.let { DeliveryStatus.valueOf(it.uppercase().trim()) }
        val fromDate = request.queryParamOrNull("fromDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val toDate = request.queryParamOrNull("toDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val searchCondition = AdminDeliverySearchCondition(
            id = id,
            riderId = riderId,
            riderName = riderName,
            orderId = orderId,
            userName = userName,
            orderNumber = orderNumber,
            storeName = storeName,
            status = status,
            fromDate = fromDate,
            toDate = toDate,
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getDeliveriesByAdminCondition(searchCondition, pageRequest)
        val deliveryResponses = responseMapper.mapToDeliveryResponses(page.content)
        val responses = SimplePage(deliveryResponses, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getDelivery(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val delivery = useCase.getDelivery(id)
        val response = responseMapper.mapToDeliveryResponse(delivery)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun changeDeliveryStatus(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<ChangeDeliveryStatusRequest>()

        useCase.changeDeliverStatus(id, body)
        return ok().buildAndAwait()
    }

}
