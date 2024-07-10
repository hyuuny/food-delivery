import hyuuny.fooddelivery.common.constant.DeliveryStatus
import java.time.LocalDateTime

data class AcceptDeliveryCommand(
    val orderId: Long,
    val riderId: Long,
    val status: DeliveryStatus,
    val createdAt: LocalDateTime,
)

data class CancelDeliveryCommand(
    val status: DeliveryStatus,
    val cancelTime: LocalDateTime,
)

data class PickupDeliveryCommand(
    val status: DeliveryStatus,
    val pickupTime: LocalDateTime,
)
