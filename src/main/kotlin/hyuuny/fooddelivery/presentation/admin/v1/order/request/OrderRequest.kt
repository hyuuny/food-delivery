import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import java.time.LocalDate

data class AdminOrderSearchCondition(
    val id: Long?,
    val orderNumber: String?,
    val userId: Long?,
    val userName: String?,
    val storeId: Long?,
    val storeName: String?,
    val categoryIds: List<Long>?,
    val paymentId: String?,
    val paymentMethod: PaymentMethod?,
    val orderStatus: OrderStatus?,
    val deliveryType: DeliveryType?,
    val phoneNumber: String?,
    val fromDate: LocalDate?,
    val toDate: LocalDate?,
)
