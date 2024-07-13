package hyuuny.fooddelivery.deliveries.domain

import AcceptDeliveryCommand
import CancelDeliveryCommand
import ChangeDeliveryStatusCommand
import DeliveredDeliveryCommand
import PickupDeliveryCommand
import hyuuny.fooddelivery.common.constant.DeliveryStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("deliveries")
class Delivery(
    id: Long? = null,
    val riderId: Long,
    val orderId: Long,
    status: DeliveryStatus,
    pickupTime: LocalDateTime? = null,
    deliveredTime: LocalDateTime? = null,
    cancelTime: LocalDateTime? = null,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var status = status
        private set
    var pickupTime = pickupTime
        private set
    var deliveredTime = deliveredTime
        private set
    var cancelTime = cancelTime
        private set

    companion object {
        fun handle(command: AcceptDeliveryCommand): Delivery = Delivery(
            riderId = command.riderId,
            orderId = command.orderId,
            status = command.status,
            createdAt = command.createdAt,
        )
    }

    fun handle(command: CancelDeliveryCommand) {
        this.status = command.status
        this.cancelTime = command.cancelTime
    }

    fun handle(command: PickupDeliveryCommand) {
        this.status = command.status
        this.pickupTime = command.pickupTime
    }

    fun handle(command: DeliveredDeliveryCommand) {
        this.status = command.status
        this.deliveredTime = command.deliveredTime
    }

    fun handle(command: ChangeDeliveryStatusCommand) {
        this.status = command.status
    }

}
