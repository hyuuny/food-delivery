package hyuuny.fooddelivery.domain.order

import CreateOrderCommand
import UpdateOrderStatusCommand
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.OrderStatus.Companion.CANCELABLE_ORDER_STATUS
import hyuuny.fooddelivery.common.constant.OrderStatus.Companion.REFUNDABLE_ORDER_STATUS
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
    val categoryId: Long,
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
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var status = status
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateOrderCommand): Order = Order(
            orderNumber = command.orderNumber,
            userId = command.userId,
            storeId = command.storeId,
            categoryId = command.categoryId,
            paymentId = command.paymentId,
            paymentMethod = command.paymentMethod,
            status = command.status,
            deliveryType = command.deliveryType,
            zipCode = command.zipCode,
            address = command.address,
            detailAddress = command.detailAddress,
            phoneNumber = command.phoneNumber,
            messageToRider = command.messageToRider,
            messageToStore = command.messageToStore,
            totalPrice = command.totalPrice,
            deliveryFee = command.deliveryFee,
            createdAt = command.createdAt,
            updatedAt = command.updatedAt,
        )
    }

    fun handle(command: UpdateOrderStatusCommand) {
        this.status = command.status
        this.updatedAt = command.updatedAt
    }

    fun isCancelable(): Boolean = CANCELABLE_ORDER_STATUS.contains(status)

    fun isRefundable(): Boolean = REFUNDABLE_ORDER_STATUS.contains(status)

}
