package hyuuny.fooddelivery.deliveries.presentation.admin.v1.response

import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.domain.StoreDetail
import hyuuny.fooddelivery.users.domain.User
import java.time.LocalDateTime

data class DeliveryResponse(
    val id: Long,
    val riderId: Long,
    val riderName: String,
    val riderPhoneNumber: String,
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
        fun from(
            entity: Delivery,
            rider: User,
            order: Order,
            store: Store,
            storeDetail: StoreDetail,
            user: User
        ): DeliveryResponse {
            return DeliveryResponse(
                id = entity.id!!,
                riderId = entity.riderId,
                riderName = rider.name,
                riderPhoneNumber = rider.phoneNumber,
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
    val id: Long,
    val riderId: Long,
    val orderId: Long,
    val orderNumber: String,
    val riderName: String,
    val storeId: Long,
    val storeName: String,
    val userId: Long,
    val userName: String,
    val status: DeliveryStatus,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Delivery, rider: User, order: Order, store: Store, user: User): DeliveryResponses =
            DeliveryResponses(
                id = entity.id!!,
                riderId = entity.riderId,
                orderId = entity.orderId,
                orderNumber = order.orderNumber,
                riderName = rider.name,
                storeId = store.id!!,
                storeName = store.name,
                userId = user.id!!,
                userName = user.name,
                status = entity.status,
                createdAt = entity.createdAt,
            )
    }
}
