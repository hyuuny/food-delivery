package hyuuny.fooddelivery.coupons.application

import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.infrastructure.CouponRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetAllIssuableCouponUseCaseTest : BehaviorSpec({

    val repository = mockk<CouponRepository>()
    val useCase = CouponUseCase(repository)

    given("발급 가능한 쿠폰 목록을 조회할 때") {
        val categoryId = 1L

        val now = LocalDateTime.now()
        val firstCoupon = Coupon(
            id = 1,
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now,
            issueEndDate = now.plusDays(7),
            validFrom = now,
            validTo = now.plusDays(7),
            createdAt = now,
        )

        val secondCoupon = Coupon(
            id = 2,
            code = "내일도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now.plusDays(1),
            issueEndDate = now.plusDays(2),
            validFrom = now,
            validTo = now.plusDays(7),
            createdAt = now,
        )

        val thirdCoupon = Coupon(
            id = 3,
            code = "모래도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now.plusDays(1),
            issueEndDate = now.plusDays(2),
            validFrom = now,
            validTo = now.plusDays(7),
            createdAt = now,
        )

        val yesterdayCoupon = Coupon(
            id = 4,
            code = "어제도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now.minusDays(1),
            issueEndDate = now.plusDays(3),
            validFrom = now,
            validTo = now.plusDays(7),
            createdAt = now,
        )
        val coupons = listOf(firstCoupon, yesterdayCoupon).sortedByDescending { it.id }

        coEvery { repository.findAllByIssueStartDateLessThanEqualAndIssueEndDateGreaterThanEqual(any()) } returns coupons

        When("발급 가능한 쿠폰 목록을 조회하면") {
            val result = useCase.getAllIssuableCoupon(now)

            then("발급 가능한 쿠폰 목록이 조회된다.") {
                result.size shouldBe 2
                result[0].id shouldBe yesterdayCoupon.id
                result[1].id shouldBe firstCoupon.id
            }
        }

        When("발급 가능한 쿠폰 목록이 없다면") {
            coEvery { repository.findAllByIssueStartDateLessThanEqualAndIssueEndDateGreaterThanEqual(any()) } returns emptyList()

            Then("빈 목록이 조회된다.") {
                val result = useCase.getAllIssuableCoupon(now.plusDays(10))
                result.size shouldBe 0
            }
        }

    }
})
