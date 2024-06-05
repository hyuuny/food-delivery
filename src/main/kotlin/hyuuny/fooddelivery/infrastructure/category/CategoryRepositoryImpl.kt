package hyuuny.fooddelivery.infrastructure.category

import AdminCategorySearchCondition
import hyuuny.fooddelivery.domain.Category
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component

@Component
class CategoryRepositoryImpl(
    private val dao: CategoryDao,
    private val template: R2dbcEntityTemplate,
) : CategoryRepository {

    override suspend fun insert(category: Category): Category = dao.save(category)

    override suspend fun findById(id: Long): Category? {
        TODO("Not yet implemented")
    }

    override suspend fun update(category: Category) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun findAllCategories(): List<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: Long): Boolean {
        TODO("Not yet implemented")
    }
}