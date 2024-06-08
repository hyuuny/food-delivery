package hyuuny.fooddelivery.infrastructure.category

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.category.Category
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CategoryDao : CoroutineCrudRepository<Category, Long> {

    suspend fun findAllByDeliveryType(deliveryType: DeliveryType): List<Category>

}