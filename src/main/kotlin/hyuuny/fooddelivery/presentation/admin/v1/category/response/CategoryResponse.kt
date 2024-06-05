package hyuuny.fooddelivery.presentation.admin.v1.category.response

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.Category
import java.time.LocalDateTime

class CategoryResponse(
    val id: Long,
    val deliveryType: DeliveryType,
    val name: String,
    val priority: Int,
    val iconImageUrl: String,
    val visible: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Category): CategoryResponse {
            return CategoryResponse(
                id = entity.id!!,
                deliveryType = entity.deliveryType,
                name = entity.name,
                priority = entity.priority,
                iconImageUrl = entity.iconImageUrl,
                visible = entity.visible,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
        }
    }
}