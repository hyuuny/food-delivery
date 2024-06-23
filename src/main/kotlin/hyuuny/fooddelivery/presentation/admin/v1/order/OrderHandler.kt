package hyuuny.fooddelivery.presentation.admin.v1.order

import AdminOrderSearchCondition
import ChangeOrderStatusRequest
import hyuuny.fooddelivery.application.order.OrderUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.convertToLocalDate
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.presentation.admin.v1.order.response.OrderResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class OrderHandler(
    private val useCase: OrderUseCase,
    private val responseMapper: OrderResponseMapper,
) {

    suspend fun getOrders(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val orderNumber = request.queryParamOrNull("orderNumber")?.takeIf { it.isNotBlank() }
        val userId = request.queryParamOrNull("userId")?.toLong()
        val userName = request.queryParamOrNull("userName")?.takeIf { it.isNotBlank() }
        val storeId = request.queryParamOrNull("storeId")?.toLong()
        val storeName = request.queryParamOrNull("storeName")?.takeIf { it.isNotBlank() }
        val categoryIds = request.queryParamOrNull("categoryIds")
            ?.let { it.split(",").mapNotNull { it.toLongOrNull() } }
        val paymentId = request.queryParamOrNull("paymentId")?.takeIf { it.isNotBlank() }
        val paymentMethod = request.queryParamOrNull("paymentMethod")?.takeIf { it.isNotBlank() }
            ?.let { PaymentMethod.valueOf(it.uppercase().trim()) }
        val orderStatus = request.queryParamOrNull("orderStatus")?.takeIf { it.isNotBlank() }
            ?.let { OrderStatus.valueOf(it.uppercase().trim()) }
        val deliveryType = request.queryParamOrNull("deliveryType")?.takeIf { it.isNotBlank() }
            ?.let { DeliveryType.valueOf(it.uppercase().trim()) }
        val phoneNumber = request.queryParamOrNull("phoneNumber")?.takeIf { it.isNotBlank() }
        val fromDate = request.queryParamOrNull("fromDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }
        val toDate = request.queryParamOrNull("toDate")?.takeIf { it.isNotBlank() }?.let { convertToLocalDate(it) }

        val searchCondition = AdminOrderSearchCondition(
            id = id,
            orderNumber = orderNumber,
            userId = userId,
            userName = userName,
            storeId = storeId,
            storeName = storeName,
            categoryIds = categoryIds,
            paymentId = paymentId,
            paymentMethod = paymentMethod,
            orderStatus = orderStatus,
            deliveryType = deliveryType,
            phoneNumber = phoneNumber,
            fromDate = fromDate,
            toDate = toDate,
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getOrderByAdminCondition(searchCondition, pageRequest)

        val orderResponses = page.content.mapNotNull { OrderResponses.from(it) }
        val response = SimplePage(orderResponses, page)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getOrder(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val order = useCase.getOrder(id)
        val response = responseMapper.mapToOrderResponse(order)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun changeOrderStatus(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val body = request.awaitBody<ChangeOrderStatusRequest>()

        useCase.changeOrderStatus(id, body)
        return ok().bodyValueAndAwait(mapOf("message" to "${id}번 주문이 '${body.orderStatus.value}' 상태로 변경되었습니다."))
    }

}
