package hyuuny.fooddelivery.orders.infrastructure

import AdminOrderSearchCondition
import ApiOrderSearchCondition
import hyuuny.fooddelivery.orders.domain.Order
import hyuuny.fooddelivery.stores.infrastructure.StoreDao
import hyuuny.fooddelivery.users.infrastructure.UserDao
import kotlinx.coroutines.flow.toList
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
class OrderRepositoryImpl(
    private val dao: OrderDao,
    private val storeDao: StoreDao,
    private val orderItemDao: OrderItemDao,
    private val userDao: UserDao,
    private val template: R2dbcEntityTemplate,
) : OrderRepository {

    override suspend fun insert(order: Order): Order = dao.save(order)

    override suspend fun findById(id: Long): Order? = dao.findById(id)

    override suspend fun findAllByIdIn(ids: List<Long>): List<Order> = dao.findAllById(ids).toList()

    override suspend fun findByIdAndUserId(id: Long, userId: Long): Order? = dao.findByIdAndUserId(id, userId)

    override suspend fun updateStatus(order: Order) {
        template.update<Order>()
            .matching(
                Query.query(
                    where("id").`is`(order.id!!)
                )
            ).applyAndAwait(
                Update.update("status", order.status)
                    .set("updatedAt", order.updatedAt)
            )
    }

    override suspend fun findAllOrders(
        searchCondition: AdminOrderSearchCondition,
        pageable: Pageable
    ): PageImpl<Order> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Order>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun findAllOrders(searchCondition: ApiOrderSearchCondition, pageable: Pageable): PageImpl<Order> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Order>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    private suspend fun buildCriteria(searchCondition: AdminOrderSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.orderNumber?.let {
            criteria = criteria.and("orderNumber").`is`(it)
        }

        searchCondition.userId?.let {
            criteria = criteria.and("userId").`is`(it)
        }

        searchCondition.userName?.let {
            val userIds = userDao.findAllByName(it).mapNotNull { it.id }
            criteria = criteria.and("userId").`in`(userIds)
        }

        searchCondition.storeId?.let {
            criteria = criteria.and("storeId").`is`(it)
        }

        searchCondition.storeName?.let {
            val storeIds = storeDao.findAllByNameLike(it).mapNotNull { it.id }
            criteria = criteria.and("storeId").`in`(storeIds)
        }

        searchCondition.categoryIds?.let {
            criteria = criteria.and("categoryId").`in`(it)
        }

        searchCondition.paymentId?.let {
            criteria = criteria.and("paymentId").`is`(it)
        }

        searchCondition.paymentMethod?.let {
            criteria = criteria.and("paymentMethod").`is`(it)
        }

        searchCondition.orderStatus?.let {
            criteria = criteria.and("status").`is`(it)
        }

        searchCondition.deliveryType?.let {
            criteria = criteria.and("deliveryType").`is`(it)
        }

        searchCondition.phoneNumber?.let {
            criteria = criteria.and("phoneNumber").`is`(it)
        }

        searchCondition.fromDate?.let {
            criteria = criteria.and("createdAt").greaterThanOrEquals(it)
        }

        searchCondition.toDate?.let {
            criteria = criteria.and("createdAt").lessThanOrEquals(it)
        }

        return criteria
    }

    private suspend fun buildCriteria(searchCondition: ApiOrderSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.userId.let {
            criteria = criteria.and("userId").`is`(it)
        }

        searchCondition.categoryIds?.let {
            criteria = criteria.and("categoryId").`in`(it)
        }

        searchCondition.deliveryType?.let {
            criteria = criteria.and("deliveryType").`is`(it)
        }

        searchCondition.orderStatus?.let {
            criteria = criteria.and("status").`is`(it)
        }

        searchCondition.storeName?.let {
            val storeIds = storeDao.findAllByNameContaining(it).mapNotNull { store -> store.id }
            criteria = criteria.and("storeId").`in`(storeIds)
        }

        searchCondition.menuName?.let {
            val orderIds = orderItemDao.findAllByMenuNameContains(it).map { item -> item.orderId }
            criteria = criteria.and("id").`in`(orderIds)
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
