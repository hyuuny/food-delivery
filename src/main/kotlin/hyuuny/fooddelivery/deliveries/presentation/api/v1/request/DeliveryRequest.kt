import hyuuny.fooddelivery.common.constant.DeliveryStatus
import java.time.LocalDate

data class AcceptDeliveryRequest(
    val orderId: Long,
    val riderId: Long,
)

data class CancelDeliveryRequest(
    val orderId: Long,
    val riderId: Long,
)

data class PickupDeliveryRequest(
    val orderId: Long,
    val riderId: Long,
)

data class DeliveredDeliveryRequest(
    val orderId: Long,
    val riderId: Long,
)

data class ApiDeliverSearchCondition(
    val id: Long?,
    val userId: Long,
    val orderNumber: String?,
    val status: DeliveryStatus?,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)