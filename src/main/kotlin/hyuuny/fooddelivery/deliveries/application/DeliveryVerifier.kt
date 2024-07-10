package hyuuny.fooddelivery.deliveries.application

import hyuuny.fooddelivery.common.constant.DeliveryStatus.Companion.CANCELABLE_DELIVERY_STATUS
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.users.domain.User

object DeliveryVerifier {

    fun verifyAccept(order: Order, rider: User) {
        if (!OrderStatus.ACCEPTABLE_DELIVERY_STATUS.contains(order.status)) throw IllegalStateException("배달 가능한 주문 상태가 아닙니다.")
        if (!rider.isRider()) throw IllegalStateException("라이더가 아닙니다.")
    }

    fun verifyCancel(delivery: Delivery, order: Order, rider: User) {
        if (delivery.orderId != order.id) throw IllegalStateException("주문 정보가 일치하지 않습니다.")
        if (delivery.riderId != rider.id) throw IllegalStateException("라이더 정보가 일치하지 않습니다.")
        if (!CANCELABLE_DELIVERY_STATUS.contains(delivery.status)) throw IllegalStateException("배달을 취소할 수 없는 상태입니다.")
        if (delivery.cancelTime != null) throw IllegalStateException("이미 취소된 배달입니다.")
    }

    fun verifyPickup(delivery: Delivery, order: Order, rider: User) {
        if (delivery.orderId != order.id) throw IllegalStateException("주문 정보가 일치하지 않습니다.")
        if (delivery.riderId != rider.id) throw IllegalStateException("라이더 정보가 일치하지 않습니다.")
        if (delivery.cancelTime != null) throw IllegalStateException("이미 취소된 배달입니다.")
    }

}
