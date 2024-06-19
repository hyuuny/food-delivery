package hyuuny.fooddelivery.domain.order

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("orders")
class Order(
    id: Long? = null,
    val orderNumber: String,
    val userId: Long,
    val storeId: Long,
    val paymentId: String,
    val paymentMethod: PaymentMethod,
    status: OrderStatus,
    val deliveryType: DeliveryType,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val phoneNumber: String,
    val messageToRider: String? = null,
    val messageToStore: String? = null,
    val totalPrice: Long,
    val deliveryFee: Long = 0,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var status = status
        private set

}
