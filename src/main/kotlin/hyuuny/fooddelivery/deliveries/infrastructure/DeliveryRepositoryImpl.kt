package hyuuny.fooddelivery.deliveries.infrastructure

import hyuuny.fooddelivery.deliveries.domain.Delivery
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component

@Component
class DeliveryRepositoryImpl(
    private val dao: DeliveryDao,
    private val template: R2dbcEntityTemplate,
) : DeliveryRepository {

    override suspend fun insert(delivery: Delivery): Delivery = dao.save(delivery)

    override suspend fun findById(id: Long): Delivery? = dao.findById(id)

    override suspend fun findAllByRiderId(riderId: Long): List<Delivery> = dao.findAllByRiderId(riderId)

    override suspend fun updatePickupTime(delivery: Delivery) {
        template.update<Delivery>()
            .matching(
                Query.query(
                    where("id").`is`(delivery.id!!),
                ),
            ).applyAndAwait(
                Update.update("status", delivery.status)
                    .set("pickupTime", delivery.pickupTime)
            )
    }

    override suspend fun updateDeliveredTime(delivery: Delivery) {
        template.update<Delivery>()
            .matching(
                Query.query(
                    where("id").`is`(delivery.id!!),
                ),
            ).applyAndAwait(
                Update.update("status", delivery.status)
                    .set("deliveredTime", delivery.deliveredTime)
            )
    }

    override suspend fun updateCancelTime(delivery: Delivery) {
        template.update<Delivery>()
            .matching(
                Query.query(
                    where("id").`is`(delivery.id!!),
                ),
            ).applyAndAwait(
                Update.update("status", delivery.status)
                    .set("cancelTime", delivery.cancelTime)
            )
    }

    override suspend fun updateStatus(delivery: Delivery) {
        TODO("Not yet implemented")
    }
}
