package hyuuny.fooddelivery.presentation.api.v1.menu.response

import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup

data class MenuResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val popularity: Boolean,
    val imageUrl: String? = null,
    val description: String? = null,
    val optionGroups: List<OptionGroupResponse>
) {
    companion object {
        fun from(menu: Menu, optionGroups: List<OptionGroupResponse>): MenuResponse {
            return MenuResponse(
                id = menu.id!!,
                name = menu.name,
                price = menu.price,
                popularity = menu.popularity,
                imageUrl = menu.imageUrl,
                description = menu.description,
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
        fun from(group: OptionGroup, options: List<OptionResponse>): OptionGroupResponse {
            return OptionGroupResponse(
                id = group.id!!,
                menuId = group.menuId,
                name = group.name,
                required = group.required,
                priority = group.priority,
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
        fun from(option: Option): OptionResponse {
            return OptionResponse(
                id = option.id!!,
                optionGroupId = option.optionGroupId,
                name = option.name,
                price = option.price
            )
        }
    }
}


