package hyuuny.fooddelivery.infrastructure.category

import AdminCategorySearchCondition
import hyuuny.fooddelivery.domain.Category
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

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun bulkUpdatePriority(categories: List<Category>) {
        TODO("Not yet implemented")
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

    override suspend fun findAllCategories(): List<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: Long): Boolean {
        TODO("Not yet implemented")
    }

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