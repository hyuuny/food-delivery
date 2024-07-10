package hyuuny.fooddelivery.deliveries.application

import AcceptDeliveryCommand
import AcceptDeliveryRequest
import CancelDeliveryCommand
import PickupDeliveryCommand
import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.deliveries.infrastructure.DeliveryRepository
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.users.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class DeliveryUseCase(
    private val repository: DeliveryRepository,
) {

    @Transactional
    suspend fun acceptDelivery(
        request: AcceptDeliveryRequest,
        getOrder: suspend () -> Order,
        getRider: suspend () -> User,
    ): Delivery {
        val now = LocalDateTime.now()
        val order = getOrder()
        val rider = getRider()
        DeliveryVerifier.verifyAccept(order, rider)

        val delivery = Delivery.handle(
            AcceptDeliveryCommand(
                orderId = order.id!!,
                riderId = rider.id!!,
                status = DeliveryStatus.ACCEPTED,
                createdAt = now,
            )
        )
        return repository.insert(delivery)
    }

    @Transactional
    suspend fun cancel(
        id: Long,
        getOrder: suspend () -> Order,
        getRider: suspend () -> User,
    ) {
        val now = LocalDateTime.now()
        val canceledOrder = getOrder()
        val rider = getRider()
        val delivery = findDeliveryByIdOrThrow(id)

        DeliveryVerifier.verifyCancel(delivery, canceledOrder, rider)

        delivery.handle(
            CancelDeliveryCommand(
                status = DeliveryStatus.CANCELED,
                cancelTime = now,
            )
        )
        repository.updateCancelTime(delivery)
    }

    @Transactional
    suspend fun pickup(
        id: Long,
        getOrder: suspend () -> Order,
        getRider: suspend () -> User,
    ) {
        val now = LocalDateTime.now()
        val order = getOrder()
        val rider = getRider()
        val delivery = findDeliveryByIdOrThrow(id)

        DeliveryVerifier.verifyPickup(delivery, order, rider)

        delivery.handle(
            PickupDeliveryCommand(
                status = DeliveryStatus.DELIVERING,
                pickupTime = now,
            )
        )
        repository.updatePickupTime(delivery)
    }

    private suspend fun findDeliveryByIdOrThrow(id: Long): Delivery =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 배달 내역을 찾을 수 없습니다.")

}
