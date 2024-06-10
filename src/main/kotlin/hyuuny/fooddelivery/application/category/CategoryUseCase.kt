package hyuuny.fooddelivery.application.category

import AdminCategorySearchCondition
import CreateCategoryCommand
import CreateCategoryRequest
import ReOrderCategoryCommand
import ReOrderCategoryRequests
import UpdateCategoryCommand
import UpdateCategoryRequest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.category.Category
import hyuuny.fooddelivery.infrastructure.category.CategoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CategoryUseCase(
    private val repository: CategoryRepository,
) {

    suspend fun getCategoriesByAdminCondition(
        searchCondition: AdminCategorySearchCondition,
        pageable: Pageable
    ): Page<Category> {
        val page = repository.findAllCategories(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun createCategory(request: CreateCategoryRequest): Category {
        val now = LocalDateTime.now()
        val category = Category.handle(
            CreateCategoryCommand(
                deliveryType = request.deliveryType,
                name = request.name,
                priority = request.priority,
                iconImageUrl = request.iconImageUrl,
                visible = request.visible,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(category)
    }

    suspend fun getCategory(id: Long): Category {
        return findCategoryByIdOrThrow(id)
    }

    suspend fun updateCategory(id: Long, request: UpdateCategoryRequest) {
        val now = LocalDateTime.now()
        val category = findCategoryByIdOrThrow(id)
        category.handle(
            UpdateCategoryCommand(
                deliveryType = request.deliveryType,
                name = request.name,
                iconImageUrl = request.iconImageUrl,
                visible = request.visible,
                updatedAt = now,
            )
        )
        repository.update(category)
    }

    suspend fun reOrderCategories(deliveryType: DeliveryType, requests: ReOrderCategoryRequests) {
        val now = LocalDateTime.now()
        val categories = repository.findAllCategoriesByDeliveryType(deliveryType)

        if (categories.size != requests.reOrderedCategories.size) throw IllegalStateException("카테고리 개수가 일치하지 않습니다.")

        val categoryMap = categories.associateBy { it.id }
        requests.reOrderedCategories.forEach {
            val category = categoryMap[it.categoryId] ?: return@forEach
            category.handle(
                ReOrderCategoryCommand(
                    priority = it.priority,
                    updatedAt = now,
                )
            )
        }
        repository.bulkUpdatePriority(categories)
    }

    suspend fun getVisibleCategoriesByDeliveryTypeOrderByPriority(deliveryType: DeliveryType): List<Category> =
        repository.findAllCategoriesByDeliveryType(deliveryType).sortedBy { it.priority }

    suspend fun deleteCategory(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("존재하지 않는 카테고리입니다.")
        repository.delete(id)
    }

    private suspend fun findCategoryByIdOrThrow(id: Long): Category {
        return repository.findById(id)
            ?: throw NoSuchElementException("존재하지 않는 카테고리입니다.")
    }

}