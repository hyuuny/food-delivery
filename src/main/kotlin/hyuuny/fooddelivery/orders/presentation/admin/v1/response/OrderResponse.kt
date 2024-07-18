package hyuuny.fooddelivery.orders.presentation.admin.v1.response

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.orders.domain.OrderItem
import hyuuny.fooddelivery.orders.domain.OrderItemOption
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val orderNumber: String,
    val userId: Long,
    val userName: String,
    val storeId: Long,
    val storeName: String,
    val categoryId: Long,
    val couponId: Long?,
    val paymentId: String,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val deliveryType: DeliveryType,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val phoneNumber: String,
    val messageToRider: String?,
    val messageToStore: String?,
    val orderPrice: Long,
    val couponDiscountAmount: Long,
    val totalPrice: Long,
    val deliveryFee: Long,
    val orderItems: List<OrderItemResponse>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            entity: Order,
            userName: String,
            storeName: String,
            orderItemResponses: List<OrderItemResponse>
        ): OrderResponse = OrderResponse(
            id = entity.id!!,
            orderNumber = entity.orderNumber,
            userId = entity.userId,
            userName = userName,
            storeId = entity.storeId,
            storeName = storeName,
            categoryId = entity.categoryId,
            couponId = entity.couponId,
            paymentId = entity.paymentId,
            paymentMethod = entity.paymentMethod,
            status = entity.status,
            deliveryType = entity.deliveryType,
            zipCode = entity.zipCode,
            address = entity.address,
            detailAddress = entity.detailAddress,
            phoneNumber = entity.phoneNumber,
            messageToRider = entity.messageToRider,
            messageToStore = entity.messageToStore,
            orderPrice = entity.orderPrice,
            couponDiscountAmount = entity.couponDiscountAmount,
            totalPrice = entity.totalPrice,
            deliveryFee = entity.deliveryFee,
            orderItems = orderItemResponses,
            createdAt = entity.createdAt,
        )
    }
}

data class OrderItemResponse(
    val id: Long,
    val orderId: Long,
    val menuId: Long,
    val menuName: String,
    val price: Long,
    val quantity: Int,
    val options: List<OrderItemOptionResponse>,
) {
    companion object {
        fun from(entity: OrderItem, itemOptions: List<OrderItemOptionResponse>): OrderItemResponse = OrderItemResponse(
            id = entity.id!!,
            orderId = entity.orderId,
            menuId = entity.menuId,
            menuName = entity.menuName,
            price = entity.menuPrice,
            quantity = entity.quantity,
            options = itemOptions,
        )
    }
}

data class OrderItemOptionResponse(
    val id: Long,
    val orderItemId: Long,
    val optionId: Long,
    val optionName: String,
    val price: Long,
) {
    companion object {
        fun from(entity: OrderItemOption): OrderItemOptionResponse = OrderItemOptionResponse(
            id = entity.id!!,
            orderItemId = entity.orderItemId,
            optionId = entity.optionId,
            optionName = entity.optionName,
            price = entity.optionPrice,
        )
    }
}

data class OrderResponses(
    val id: Long,
    val orderNumber: String,
    val userId: Long,
    val storeId: Long,
    val categoryId: Long,
    val paymentId: String,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val deliveryType: DeliveryType,
    val phoneNumber: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Order): OrderResponses = OrderResponses(
            id = entity.id!!,
            orderNumber = entity.orderNumber,
            userId = entity.userId,
            storeId = entity.storeId,
            categoryId = entity.categoryId,
            paymentId = entity.paymentId,
            paymentMethod = entity.paymentMethod,
            status = entity.status,
            deliveryType = entity.deliveryType,
            phoneNumber = entity.phoneNumber,
            createdAt = entity.createdAt,
        )
    }
}
