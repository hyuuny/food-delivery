package hyuuny.fooddelivery.categories.presentation.api.v1.response

import hyuuny.fooddelivery.categories.domain.Category
import hyuuny.fooddelivery.common.constant.DeliveryType

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
