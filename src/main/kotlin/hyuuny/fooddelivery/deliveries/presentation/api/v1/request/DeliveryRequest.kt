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