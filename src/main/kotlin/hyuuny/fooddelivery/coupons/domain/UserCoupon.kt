package hyuuny.fooddelivery.coupons.domain

import hyuuny.fooddelivery.coupons.application.command.IssueUserCouponCommand
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("user_coupons")
class UserCoupon(
    id: Long? = null,
    val userId: Long,
    val couponId: Long,
    val used: Boolean = false,
    val usedDate: LocalDateTime? = null,
    val issuedDate: LocalDateTime,
) {

    @Id
    var id = id
        protected set

    companion object {
        fun handle(command: IssueUserCouponCommand): UserCoupon = UserCoupon(
            userId = command.userId,
            couponId = command.couponId,
            issuedDate = command.issuedDate,
        )
    }

}