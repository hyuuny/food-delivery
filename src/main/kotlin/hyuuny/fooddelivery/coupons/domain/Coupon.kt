package hyuuny.fooddelivery.coupons.domain

import hyuuny.fooddelivery.common.constant.CouponType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("coupons")
class Coupon(
    id: Long? = null,
    val code: String,
    val type: CouponType,
    val name: String,
    val discountAmount: Long,
    val maximumDiscountAmount: Long,
    val minimumOrderAmount: Long,
    val description: String,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

}