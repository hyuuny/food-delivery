package hyuuny.fooddelivery.presentation.admin.v1.optiongroup.response

import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import java.time.LocalDateTime

data class OptionGroupResponse(
    val id: Long,
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val priority: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object{
        fun from(entity: OptionGroup): OptionGroupResponse = OptionGroupResponse(
            id = entity.id!!,
            menuId = entity.menuId,
            name = entity.name,
            required = entity.required,
            priority = entity.priority,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}

data class OptionGroupResponses(
    val id: Long,
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val priority: Int,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(entity: OptionGroup): OptionGroupResponses = OptionGroupResponses(
            id = entity.id!!,
            menuId = entity.menuId,
            name = entity.name,
            required = entity.required,
            priority = entity.priority,
            createdAt = entity.createdAt,
        )
    }
}
