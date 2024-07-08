package hyuuny.fooddelivery.categories.infrastructure

import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.common.constant.DeliveryType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CategoryDao : CoroutineCrudRepository<Category, Long> {

    suspend fun findAllByDeliveryType(deliveryType: DeliveryType): List<Category>

}
