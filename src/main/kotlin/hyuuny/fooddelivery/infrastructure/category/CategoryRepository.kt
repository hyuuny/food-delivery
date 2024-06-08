package hyuuny.fooddelivery.infrastructure.category

import AdminCategorySearchCondition
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.category.Category
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface CategoryRepository {

    suspend fun insert(category: Category): Category

    suspend fun findById(id: Long): Category?

    suspend fun update(category: Category)

    suspend fun delete(id: Long)

    suspend fun bulkUpdatePriority(categories: List<Category>)

    suspend fun findAllCategories(searchCondition: AdminCategorySearchCondition, pageable: Pageable): PageImpl<Category>

    suspend fun findAllCategories(): List<Category>

    suspend fun findAllCategoriesByDeliveryType(deliveryType: DeliveryType): List<Category>

    suspend fun existsById(id: Long): Boolean

}