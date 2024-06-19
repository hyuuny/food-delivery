import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod

data class CreateOrderRequest(
    val storeId: Long,
    val paymentMethod: PaymentMethod,
    val deliveryType: DeliveryType,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val phoneNumber: String,
    val messageToRider: String?,
    val messageToStore: String?,
    val orderItems: List<CreateOrderItemRequest>,
    val totalPrice: Long,
    val deliveryFee: Long,
)

data class CreateOrderItemRequest(
    val menuId: Long,
    val quantity: Int,
    val optionIds: List<Long>,
)

data class ApiOrderSearchCondition(
    val id: Long?,
    val orderNumber: String?,
    val userId: Long?,
    val storeId: Long?,
    val paymentId: String?,
    val paymentMethod: PaymentMethod?,
    val status: OrderStatus?,
    val deliveryType: DeliveryType,
    val phoneNumber: String?,
)
