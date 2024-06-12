package hyuuny.fooddelivery.domain.store

import CreateStoreDetailCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("store_details")
class StoreDetail(
    id: Long? = null,
    val storeId: Long,
    val zipCode: String,
    val address: String,
    val detailedAddress: String? = null,
    val openHours: String? = null,
    val closedDay: String? = null,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

    companion object {
        fun handle(command: CreateStoreDetailCommand): StoreDetail = StoreDetail(
            storeId = command.storeId,
            zipCode = command.zipCode,
            address = command.address,
            detailedAddress = command.detailedAddress,
            openHours = command.openHours,
            closedDay = command.closedDay,
            createdAt = command.createdAt,
        )
    }

}