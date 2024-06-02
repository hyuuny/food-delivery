package hyuuny.fooddelivery.infrastructure.store

import StoreSearchCondition
import hyuuny.fooddelivery.domain.store.Store
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import selectAndCount

@Component
class StoreRepositoryImpl(
    private val dao: StoreDao,
    private val template: R2dbcEntityTemplate,
) : StoreRepository {

    override suspend fun insert(store: Store): Store = dao.save(store)

    override suspend fun findById(id: Long): Store? = dao.findById(id)

    override suspend fun update(store: Store) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(store: Store) {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun findAllStores(searchCondition: StoreSearchCondition, pageable: Pageable): PageImpl<Store> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Store>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    private fun buildCriteria(searchCondition: StoreSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.categoryId?.let {
            criteria = criteria.and("category_id").`is`(it)
        }

        searchCondition.deliveryType?.let {
            criteria = criteria.and("delivery_type").`is`(it)
        }

        searchCondition.name?.let {
            criteria = criteria.and("name").like("%$it%")
        }

        searchCondition.taxId?.let {
            criteria = criteria.and("tax_id").`is`(it)
        }

        searchCondition.phoneNumber?.let {
            criteria = criteria.and("phone_number").like("%$it%")
        }

        return criteria
    }
}