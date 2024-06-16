package hyuuny.fooddelivery.domain.useraddress

import ChangeUserAddressSelectedCommand
import CreateUserAddressCommand
import UpdateUserAddressCommand
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
    selected: Boolean = false,
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
    var selected = selected
        private set
    var updatedAt = updatedAt
        private set

    companion object {

        const val MAX_USER_ADDRESS_COUNT = 10

        fun handle(command: CreateUserAddressCommand): UserAddress = UserAddress(
            userId = command.userId,
            name = command.name,
            zipCode = command.zipCode,
            address = command.address,
            detailAddress = command.detailAddress,
            messageToRider = command.messageToRider,
            entrancePassword = command.entrancePassword,
            routeGuidance = command.routeGuidance,
            selected = command.selected,
            createdAt = command.createdAt,
            updatedAt = command.updatedAt,
        )
    }

    fun handle(command: ChangeUserAddressSelectedCommand) {
        this.selected = command.selected
        this.updatedAt = command.updatedAt
    }

    fun handle(command: UpdateUserAddressCommand) {
        this.name = command.name
        this.zipCode = command.zipCode
        this.address = command.address
        this.detailAddress = command.detailAddress
        this.messageToRider = command.messageToRider
        this.entrancePassword = command.entrancePassword
        this.routeGuidance = command.routeGuidance
        this.updatedAt = command.updatedAt
    }

}
