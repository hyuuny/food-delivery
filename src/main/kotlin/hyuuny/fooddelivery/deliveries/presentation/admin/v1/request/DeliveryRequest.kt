import hyuuny.fooddelivery.common.constant.DeliveryStatus
import java.time.LocalDate

data class ChangeDeliveryStatusRequest(
    val status: DeliveryStatus,
)

data class AdminDeliverySearchCondition(
    val id: Long?,
    val riderId: Long?,
    val riderName: String?,
    val orderId: Long?,
    val orderNumber: String?,
    val userName: String?,
    val storeName: String?,
    val status: DeliveryStatus?,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)
