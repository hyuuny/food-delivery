package hyuuny.fooddelivery.categories.presentation.admin.v1.response

import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.common.constant.DeliveryType
import java.time.LocalDateTime

data class CategoryResponse(
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

data class CategoryResponses(
    val id: Long,
    val deliveryType: DeliveryType,
    val name: String,
    val priority: Int,
    val visible: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: Category): CategoryResponses {
            return CategoryResponses(
                id = entity.id!!,
                deliveryType = entity.deliveryType,
                name = entity.name,
                priority = entity.priority,
                visible = entity.visible,
                createdAt = entity.createdAt,
            )
        }
    }
}
