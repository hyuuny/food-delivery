package hyuuny.fooddelivery.infrastructure.category

import hyuuny.fooddelivery.domain.Category
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CategoryDao : CoroutineCrudRepository<Category, Long> {
}