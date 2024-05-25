package hyuuny.fooddelivery.presentation.admin.v1.option.response

import hyuuny.fooddelivery.domain.option.Option
import java.time.LocalDateTime

data class OptionResponse(
    val id: Long,
    val optionGroupId: Long,
    val name: String,
    val price: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    constructor(entity: Option) : this(
        id = entity.id!!,
        optionGroupId = entity.optionGroupId,
        name = entity.name,
        price = entity.price,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
}

data class OptionResponses(
    val id: Long,
    val optionGroupId: Long,
    val name: String,
    val price: Long,
    val createdAt: LocalDateTime,
){
    constructor(entity: Option) : this(
        id = entity.id!!,
        optionGroupId = entity.optionGroupId,
        name = entity.name,
        price = entity.price,
        createdAt = entity.createdAt,
    )
}