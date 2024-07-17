package hyuuny.fooddelivery.coupons.domain

import CreateCouponCommand
import hyuuny.fooddelivery.common.constant.CouponType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("coupons")
class Coupon(
    id: Long? = null,
    val code: String,
    val type: CouponType,
    val categoryId: Long?,
    val storeId: Long?,
    val name: String,
    val discountAmount: Long,
    val minimumOrderAmount: Long,
    val description: String,
    val issueStartDate: LocalDateTime,
    val issueEndDate: LocalDateTime,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val createdAt: LocalDateTime,
) {

    @Id
    var id = id
        protected set

    companion object {
        fun handle(command: CreateCouponCommand): Coupon = Coupon(
            code = command.code,
            type = command.type,
            categoryId = command.categoryId,
            storeId = command.storeId,
            name = command.name,
            discountAmount = command.discountAmount,
            minimumOrderAmount = command.minimumOrderAmount,
            description = command.description,
            issueStartDate = command.issueStartDate,
            issueEndDate = command.issueEndDate,
            validFrom = command.validFrom,
            validTo = command.validTo,
            createdAt = command.createdAt,
        )
    }

    fun getIssuancePeriod(): ClosedRange<LocalDateTime> = issueStartDate..issueEndDate

    fun isApplicableForOrder(categoryId: Long, storeId: Long): Boolean {
        return when (type) {
            CouponType.STORE -> this.storeId == storeId
            CouponType.CATEGORY -> this.categoryId == categoryId
        }
    }

}