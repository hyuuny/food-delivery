package hyuuny.fooddelivery.domain.store

import CreateStoreCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "store")
class Store(
    id: Long? = null,
    categoryId: Long,
    deliveryType: DeliveryType,
    name: String,
    ownerName: String,
    taxId: String,
    deliveryFee: Long = 0,
    minimumOrderAmount: Long,
    iconImageUrl: String? = null,
    description: String,
    foodOrigin: String,
    phoneNumber: String,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var categoryId = categoryId
        private set
    var deliveryType = deliveryType
        private set
    var name = name
        private set
    var ownerName = ownerName
        private set
    var taxId = taxId
        private set
    var deliveryFee = deliveryFee
        private set
    var minimumOrderAmount = minimumOrderAmount
        private set
    var iconImageUrl = iconImageUrl
        private set
    var description = description
        private set
    var foodOrigin = foodOrigin
        private set
    var phoneNumber = phoneNumber
        private set
    var updatedAt = updatedAt
        private set

    companion object {
        fun handle(command: CreateStoreCommand): Store {
            return Store(
                categoryId = command.categoryId,
                deliveryType = command.deliveryType,
                name = command.name,
                ownerName = command.ownerName,
                taxId = command.taxId,
                deliveryFee = command.deliveryFee,
                minimumOrderAmount = command.minimumOrderAmount,
                iconImageUrl = command.iconImageUrl,
                description = command.description,
                foodOrigin = command.foodOrigin,
                phoneNumber = command.phoneNumber,
                createdAt = command.createdAt,
                updatedAt = command.updatedAt
            )
        }
    }

}

enum class DeliveryType(val title: String) {
    SELF("자체배달"),
    OUTSOURCING("배달업체 이용"),
    TAKE_OUT("포장")
}