package hyuuny.fooddelivery.coupons.application

import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.infrastructure.CouponRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetCouponUseCaseTest : BehaviorSpec({

    val repository = mockk<CouponRepository>()
    val useCase = CouponUseCase(repository)

    given("쿠폰을 상세조회 할 때") {
        val id = 1L

        val now = LocalDateTime.now()
        val coupon = Coupon(
            id = id,
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now.plusDays(1),
            issueEndDate = now.plusDays(7),
            validFrom = now.plusDays(1),
            validTo = now.plusDays(7),
            createdAt = now,
        )
        coEvery { repository.findById(any()) } returns coupon

        `when`("존재하는 쿠폰이면") {
            val result = useCase.getCoupon(id)

            then("쿠폰을 조회할 수 있다.") {
                result.id.shouldNotBeNull()
                result.code shouldBe coupon.code
                result.type shouldBe coupon.type
                result.name shouldBe coupon.name
                result.discountAmount shouldBe coupon.discountAmount
                result.minimumOrderAmount shouldBe coupon.minimumOrderAmount
                result.description shouldBe coupon.description
                result.issueStartDate shouldBe coupon.issueStartDate
                result.issueEndDate shouldBe coupon.issueEndDate
                result.validFrom shouldBe coupon.validFrom
                result.validTo shouldBe coupon.validTo
                result.createdAt.shouldNotBeNull()
            }
        }

        When("존재하지 않는 쿠폰이면") {
            coEvery { repository.findById(any()) } returns null

            Then("쿠폰을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getCoupon(0)
                }
                ex.message shouldBe "0번 쿠폰을 찾을 수 없습니다."
            }
        }

    }
})
