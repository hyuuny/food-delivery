package hyuuny.fooddelivery.coupons.application

import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.infrastructure.UserCouponRepository
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.IssueUserCouponRequest
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetUserCouponUseCaseTest : BehaviorSpec({

    val repository = mockk<UserCouponRepository>()
    val useCase = UserCouponUseCase(repository)
    val userUseCase = mockk<UserUseCase>()
    val couponUseCase = mockk<CouponUseCase>()

    given("회원이 자신이 갖고 있는 쿠폰을 조회할 때") {
        val userId = 1L
        val couponId = 3L
        val categoryId = 1L

        val now = LocalDateTime.now()
        val user = User(
            id = userId,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )
        val coupon = Coupon(
            id = couponId,
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now.minusDays(1),
            issueEndDate = now.plusDays(7),
            validFrom = now.minusDays(1),
            validTo = now.plusDays(7),
            createdAt = now,
        )

        val request = IssueUserCouponRequest(
            userId = userId,
            couponId = couponId
        )

        val userCoupon = UserCoupon(
            id = 1,
            userId = userId,
            couponId = couponId,
            issuedDate = now,
            validFrom = coupon.validFrom,
            validTo = coupon.validTo,
            used = true,
            usedDate = now.plusMinutes(30)
        )
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { couponUseCase.getCoupon(any()) } returns coupon
        coEvery { repository.findByUserIdAndCouponId(any(), any()) } returns userCoupon

        `when`("존재하는 회원의 쿠폰이면") {
            val result = useCase.getUserCoupon(couponId) { user }

            then("정상적으로 조회할 수 있다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe request.userId
                result.couponId shouldBe request.couponId
                result.used shouldBe userCoupon.used
                result.usedDate shouldBe userCoupon.usedDate
                result.validFrom shouldBe coupon.validFrom
                result.validTo shouldBe coupon.validTo
                result.issuedDate shouldBe userCoupon.issuedDate
            }
        }

        When("존재하지 않는 회원이면") {
            coEvery { userUseCase.getUser(any()) } throws NoSuchElementException("0번 회원을 찾을 수 없습니다.")

            Then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getUserCoupon(couponId) { userUseCase.getUser(0) }
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }

        When("회원이 소유하지 않은 쿠폰이면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { couponUseCase.getCoupon(any()) } returns coupon
            coEvery { repository.findByUserIdAndCouponId(any(), any()) } returns null

            Then("회원의 쿠폰을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getUserCoupon(couponId) { user }
                }
                ex.message shouldBe "${userId}번 회원의 ${couponId}번 쿠폰을 찾을 수 없습니다."
            }
        }

    }
})
