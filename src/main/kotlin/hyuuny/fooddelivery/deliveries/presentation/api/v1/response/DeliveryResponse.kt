package hyuuny.fooddelivery.deliveries.presentation.api.v1.response

import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.users.domain.User
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
) {
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

data class DeliveryResponses(
    val riderId: Long,
    val riderName: String,
    val details: SimplePage<DeliveryDetailResponses>
) {
    companion object {
        fun from(rider: User, details: SimplePage<DeliveryDetailResponses>): DeliveryResponses =
            DeliveryResponses(
                riderId = rider.id!!,
                riderName = rider.name,
                details = details,
            )
    }
}

data class DeliveryDetailResponses(
    val id: Long,
    val orderId: Long,
    val orderNumber: String,
    val storeName: String,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val phoneNumber: String,
    val messageToRider: String? = null,
    val totalPrice: Long,
    val deliveryFee: Long = 0,
) {
    companion object {
        fun from(delivery: Delivery, order: Order, store: Store): DeliveryDetailResponses = DeliveryDetailResponses(
            id = delivery.id!!,
            orderId = order.id!!,
            orderNumber = order.orderNumber,
            storeName = store.name,
            zipCode = order.zipCode,
            address = order.address,
            detailAddress = order.detailAddress,
            phoneNumber = order.phoneNumber,
            messageToRider = order.messageToRider,
            totalPrice = order.totalPrice,
            deliveryFee = order.deliveryFee,
        )
    }
}
