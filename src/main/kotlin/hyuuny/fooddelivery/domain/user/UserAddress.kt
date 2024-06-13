package hyuuny.fooddelivery.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("user_addresses")
class UserAddress(
    id: Long? = null,
    val userId: Long,
    name: String? = null,
    zipCode: String,
    address: String,
    detailAddress: String,
    messageToRider: String? = null,
    entrancePassword: String? = null,
    routeGuidance: String? = null,
    val createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) {

    @Id
    var id = id
        private set
    var name = name
        private set
    var zipCode = zipCode
        private set
    var address = address
        private set
    var detailAddress = detailAddress
        private set
    var messageToRider = messageToRider
        private set
    var entrancePassword = entrancePassword
        private set
    var routeGuidance = routeGuidance
        private set
    var updatedAt = updatedAt
        private set

}
