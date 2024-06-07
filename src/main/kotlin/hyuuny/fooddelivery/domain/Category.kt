package hyuuny.fooddelivery.domain

import CreateCategoryCommand
import UpdateCategoryCommand
import hyuuny.fooddelivery.common.constant.DeliveryType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("categories")
class Category(
    id: Long? = null,
    deliveryType: DeliveryType,
    name: String,
    priority: Int,
    iconImageUrl: String,
    visible: Boolean = true,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime
) {

    @Id
    var id = id
        protected set
    var deliveryType = deliveryType
        private set
    var name = name
        private set
    var priority = priority
        private set
    var iconImageUrl = iconImageUrl
        private set
    var visible = visible
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateCategoryCommand): Category {
            return Category(
                deliveryType = command.deliveryType,
                name = command.name,
                priority = command.priority,
                iconImageUrl = command.iconImageUrl,
                visible = command.visible,
                createdAt = command.createdAt,
                updatedAt = command.updatedAt
            )
        }
    }

    fun handle(command: UpdateCategoryCommand) {
        this.deliveryType = command.deliveryType
        this.name = command.name
        this.iconImageUrl = command.iconImageUrl
        this.visible = command.visible
        this.updatedAt = command.updatedAt
    }

}