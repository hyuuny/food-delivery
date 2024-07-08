package hyuuny.fooddelivery.menus.presentation.api.v1.response

import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import hyuuny.fooddelivery.options.domain.Option

data class MenuResponse(
    val id: Long,
    val menuGroupId: Long,
    val name: String,
    val price: Long,
    val popularity: Boolean,
    val imageUrl: String? = null,
    val description: String? = null,
    val optionGroups: List<OptionGroupResponse>
) {
    companion object {
        fun from(entity: Menu, optionGroups: List<OptionGroupResponse>): MenuResponse {
            return MenuResponse(
                id = entity.id!!,
                menuGroupId = entity.menuGroupId,
                name = entity.name,
                price = entity.price,
                popularity = entity.popularity,
                imageUrl = entity.imageUrl,
                description = entity.description,
                optionGroups = optionGroups
            )
        }
    }
}

data class OptionGroupResponse(
    val id: Long,
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val priority: Int,
    val options: List<OptionResponse>,
) {
    companion object {
        fun from(entity: OptionGroup, options: List<OptionResponse>): OptionGroupResponse {
            return OptionGroupResponse(
                id = entity.id!!,
                menuId = entity.menuId,
                name = entity.name,
                required = entity.required,
                priority = entity.priority,
                options = options
            )
        }
    }
}

data class OptionResponse(
    val id: Long,
    val optionGroupId: Long,
    val name: String,
    val price: Long,
) {
    companion object {
        fun from(entity: Option): OptionResponse {
            return OptionResponse(
                id = entity.id!!,
                optionGroupId = entity.optionGroupId,
                name = entity.name,
                price = entity.price
            )
        }
    }
}


