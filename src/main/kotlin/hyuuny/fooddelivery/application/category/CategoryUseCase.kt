package hyuuny.fooddelivery.application.category

import CreateCategoryCommand
import CreateCategoryRequest
import hyuuny.fooddelivery.domain.Category
import hyuuny.fooddelivery.infrastructure.category.CategoryRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CategoryUseCase(
    private val repository: CategoryRepository,
) {

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
        return findCategoryByIdOrThrows(id)
    }

    private suspend fun findCategoryByIdOrThrows(id: Long): Category {
        return repository.findById(id)
            ?: throw NoSuchElementException("존재하지 않는 카테고리입니다.")
    }

}