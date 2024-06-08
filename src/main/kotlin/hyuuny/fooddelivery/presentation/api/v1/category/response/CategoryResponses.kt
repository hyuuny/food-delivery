package hyuuny.fooddelivery.presentation.api.v1.category.response

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.Category

data class CategoryResponses(
    val id: Long,
    val deliveryType: DeliveryType,
    val name: String,
    val priority: Int,
    val iconImageUrl: String,
    val visible: Boolean,
) {
    companion object {
        fun from(entity: Category) = CategoryResponses(
            id = entity.id!!,
            deliveryType = entity.deliveryType,
            name = entity.name,
            priority = entity.priority,
            iconImageUrl = entity.iconImageUrl,
            visible = entity.visible,
        )
    }
}
