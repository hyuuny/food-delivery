package hyuuny.fooddelivery.coupons.domain

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

}