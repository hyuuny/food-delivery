package hyuuny.fooddelivery.deliveries.infrastructure

import ApiDeliverSearchCondition
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.orders.infrastructure.OrderDao
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import selectAndCount

@Component
class DeliveryRepositoryImpl(
    private val dao: DeliveryDao,
    private val orderDao: OrderDao,
    private val template: R2dbcEntityTemplate,
) : DeliveryRepository {

    override suspend fun insert(delivery: Delivery): Delivery = dao.save(delivery)

    override suspend fun findById(id: Long): Delivery? = dao.findById(id)

    override suspend fun findAllDeliveries(
        searchCondition: ApiDeliverSearchCondition,
        pageable: Pageable
    ): PageImpl<Delivery> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Delivery>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

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

    private suspend fun buildCriteria(searchCondition: ApiDeliverSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.orderNumber?.let {
            val orderId = orderDao.findByOrderNumber(it)?.id
            criteria = criteria.and("orderId").`in`(orderId)
        }

        searchCondition.status?.let {
            criteria = criteria.and("status").`is`(it)
        }

        searchCondition.fromDate?.let {
            criteria = criteria.and("createdAt").greaterThanOrEquals(it)
        }

        searchCondition.toDate?.let {
            criteria = criteria.and("createdAt").lessThanOrEquals(it)
        }

        return criteria
    }
}
