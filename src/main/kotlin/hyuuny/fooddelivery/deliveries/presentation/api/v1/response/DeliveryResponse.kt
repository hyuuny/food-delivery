package hyuuny.fooddelivery.deliveries.presentation.api.v1.response

import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.deliveries.domain.Delivery
import java.time.LocalDateTime

data class DeliveryResponse(
    val id: Long,
    val riderId: Long,
    val orderId: Long,
    val status: DeliveryStatus,
    val pickupTime: LocalDateTime?,
    val deliveredTime: LocalDateTime?,
    val cancelTime: LocalDateTime?,
    val createdAt: LocalDateTime,
){
    companion object {
        fun from(entity: Delivery): Delivery {
            return Delivery(
                id = entity.id!!,
                riderId = entity.riderId,
                orderId = entity.orderId,
                status = entity.status,
                pickupTime = entity.pickupTime,
                deliveredTime = entity.deliveredTime,
                cancelTime = entity.cancelTime,
                createdAt = entity.createdAt,
            )
        }
    }
}
