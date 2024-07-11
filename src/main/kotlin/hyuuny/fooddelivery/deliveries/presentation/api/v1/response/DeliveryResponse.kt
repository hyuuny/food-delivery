package hyuuny.fooddelivery.deliveries.presentation.api.v1.response

import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.domain.StoreDetail
import hyuuny.fooddelivery.users.domain.User
import java.time.LocalDateTime

data class DeliveryResponse(
    val id: Long,
    val riderId: Long,
    val orderId: Long,
    val orderNumber: String,
    val storeName: String,
    val storeZipCode: String,
    val storeAddress: String,
    val storeDetailAddress: String?,
    val storePhoneNumber: String,
    val userName: String,
    val userZipCode: String,
    val userAddress: String,
    val userDetailAddress: String,
    val userPhoneNumber: String,
    val messageToRider: String? = null,
    val totalPrice: Long,
    val deliveryFee: Long,
    val status: DeliveryStatus,
    val pickupTime: LocalDateTime?,
    val deliveredTime: LocalDateTime?,
    val cancelTime: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Delivery, order: Order, store: Store, storeDetail: StoreDetail, user: User): DeliveryResponse {
            return DeliveryResponse(
                id = entity.id!!,
                riderId = entity.riderId,
                orderId = entity.orderId,
                orderNumber = order.orderNumber,
                storeName = store.name,
                storeZipCode = storeDetail.zipCode,
                storeAddress = storeDetail.address,
                storeDetailAddress = storeDetail.detailedAddress,
                storePhoneNumber = store.phoneNumber,
                userName = user.name,
                userZipCode = order.zipCode,
                userAddress = order.address,
                userDetailAddress = order.detailAddress,
                userPhoneNumber = order.phoneNumber,
                messageToRider = order.messageToRider,
                totalPrice = order.totalPrice,
                deliveryFee = order.deliveryFee,
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
    val storeName: String,
    val phoneNumber: String,
    val totalPrice: Long,
    val deliveryFee: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Delivery, order: Order, store: Store): DeliveryDetailResponses = DeliveryDetailResponses(
            id = entity.id!!,
            orderId = order.id!!,
            storeName = store.name,
            phoneNumber = order.phoneNumber,
            totalPrice = order.totalPrice,
            deliveryFee = order.deliveryFee,
            createdAt = entity.createdAt,
        )
    }
}
