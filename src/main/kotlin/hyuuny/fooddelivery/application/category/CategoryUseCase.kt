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
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
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

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
    suspend fun deleteCategory(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 카테고리를 찾을 수 없습니다.")
        repository.delete(id)
    }

    private suspend fun findCategoryByIdOrThrow(id: Long): Category {
        return repository.findById(id)
            ?: throw NoSuchElementException("${id}번 카테고리를 찾을 수 없습니다.")
    }

}