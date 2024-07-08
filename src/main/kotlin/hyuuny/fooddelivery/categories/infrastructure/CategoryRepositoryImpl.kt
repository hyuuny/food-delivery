package hyuuny.fooddelivery.categories.infrastructure

import AdminCategorySearchCondition
import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.common.constant.DeliveryType
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirstOrElse
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import selectAndCount
import java.time.format.DateTimeFormatter

@Component
class CategoryRepositoryImpl(
    private val dao: CategoryDao,
    private val template: R2dbcEntityTemplate,
) : CategoryRepository {

    override suspend fun insert(category: Category): Category = dao.save(category)

    override suspend fun findById(id: Long): Category? = dao.findById(id)

    override suspend fun update(category: Category) {
        template.update<Category>()
            .matching(
                Query.query(
                    Criteria.where("id").`is`(category.id!!)
                ),
            ).applyAndAwait(
                Update.update("deliveryType", category.deliveryType)
                    .set("name", category.name)
                    .set("iconImageUrl", category.iconImageUrl)
                    .set("visible", category.visible)
                    .set("updatedAt", category.updatedAt)
            )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun bulkUpdatePriority(categories: List<Category>) {
        if (categories.isEmpty()) return

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val query = """
            WITH updates (id, priority, updated_at) AS (
                VALUES ${categories.joinToString(", ") { "(${it.id}, ${it.priority}, '${it.updatedAt.format(formatter)}'::timestamp)" }}
            )
            UPDATE categories
            SET 
                priority = updates.priority, 
                updated_at = updates.updated_at
            FROM updates
            WHERE categories.id = updates.id
        """

        template.databaseClient.sql(query)
            .fetch()
            .rowsUpdated()
            .awaitFirstOrElse { throw RuntimeException("Batch category update failed") }
    }

    override suspend fun findAllCategories(
        searchCondition: AdminCategorySearchCondition,
        pageable: Pageable
    ): PageImpl<Category> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Category>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun findAllCategories(): List<Category> = dao.findAll().toList()

    override suspend fun findAllCategoriesByDeliveryType(deliveryType: DeliveryType): List<Category> =
        dao.findAllByDeliveryType(deliveryType)

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    private fun buildCriteria(searchCondition: AdminCategorySearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.deliveryType?.let {
            criteria = criteria.and("deliveryType").`is`(it)
        }

        searchCondition.name?.let {
            criteria = criteria.and("name").like("%$it%")
        }

        searchCondition.visible?.let {
            criteria = criteria.and("visible").`is`(it)
        }

        return criteria
    }
}
