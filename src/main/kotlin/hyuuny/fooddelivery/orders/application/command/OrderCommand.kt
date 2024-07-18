import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import java.time.LocalDateTime

data class CreateOrderCommand(
    val orderNumber: String,
    val userId: Long,
    val storeId: Long,
    val categoryId: Long,
    val couponId: Long?,
    val paymentId: String,
    val paymentMethod: PaymentMethod,
    val deliveryType: DeliveryType,
    val status: OrderStatus,
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
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class CreateOrderItemCommand(
    val orderId: Long,
    val menuId: Long,
    val menuName: String,
    val menuPrice: Long,
    val quantity: Int,
    val createdAt: LocalDateTime,
)

data class CreateOrderItemOptionCommand(
    val orderItemId: Long,
    val optionId: Long,
    val optionName: String,
    val optionPrice: Long,
    val createdAt: LocalDateTime,
)

data class UpdateOrderStatusCommand(
    val status: OrderStatus,
    val updatedAt: LocalDateTime,
)