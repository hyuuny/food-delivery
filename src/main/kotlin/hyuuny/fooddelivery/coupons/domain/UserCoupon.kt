package hyuuny.fooddelivery.coupons.domain

import hyuuny.fooddelivery.coupons.application.command.CancelUseCouponCommand
import hyuuny.fooddelivery.coupons.application.command.IssueUserCouponCommand
import hyuuny.fooddelivery.coupons.application.command.UseCouponCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("user_coupons")
class UserCoupon(
    id: Long? = null,
    val userId: Long,
    val couponId: Long,
    used: Boolean = false,
    usedDate: LocalDateTime? = null,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val issuedDate: LocalDateTime,
) {

    @Id
    var id = id
        protected set
    var used = used
        private set
    var usedDate = usedDate
        private set

    companion object {
        fun handle(command: IssueUserCouponCommand): UserCoupon = UserCoupon(
            userId = command.userId,
            couponId = command.couponId,
            issuedDate = command.issuedDate,
            validFrom = command.validFrom,
            validTo = command.validTo,
        )
    }

    fun isAvailable(): Boolean {
        val now = LocalDateTime.now()
        val isValidTime = now in validFrom..validTo
        return !used && isValidTime
    }

    fun handle(command: UseCouponCommand) {
        this.used = command.used
        this.usedDate = command.useDate
    }

    fun handle(command: CancelUseCouponCommand) {
        this.used = command.used
        this.usedDate = command.usedDate
    }

}