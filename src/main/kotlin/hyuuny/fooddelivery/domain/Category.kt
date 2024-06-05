package hyuuny.fooddelivery.domain

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

}