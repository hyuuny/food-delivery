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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class IssueUserCouponUseCaseTest : BehaviorSpec({

    val repository = mockk<UserCouponRepository>()
    val useCase = UserCouponUseCase(repository)
    val userUseCase = mockk<UserUseCase>()
    val couponUseCase = mockk<CouponUseCase>()

    given("회원이 쿠폰을 발급 받을 때") {
        val userId = 1L
        val couponId = 3L

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
            id = 1,
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
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
        )
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { couponUseCase.getCoupon(any()) } returns coupon
        coEvery { repository.findByUserIdAndCouponId(any(), any()) } returns null
        coEvery { repository.insert(any()) } returns userCoupon

        `when`("발급 기간내 처음 발급받는다면") {
            val result = useCase.issueCoupon({ user }, { coupon })

            then("정상적으로 쿠폰이 발급된다.") {
                result.id.shouldNotBeNull()
                result.userId shouldBe request.userId
                result.couponId shouldBe request.couponId
                result.used shouldBe false
                result.usedDate.shouldBeNull()
                result.issuedDate.shouldNotBeNull()
            }
        }

        When("존재하지 않는 회원이면") {
            coEvery { userUseCase.getUser(any()) } throws NoSuchElementException("0번 회원을 찾을 수 없습니다.")

            Then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.issueCoupon({ userUseCase.getUser(0) }, { coupon })
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }

        When("존재하지 않는 쿠폰이면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { couponUseCase.getCoupon(any()) } throws NoSuchElementException("0번 쿠폰을 찾을 수 없습니다.")

            Then("쿠폰을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.issueCoupon({ user }, { couponUseCase.getCoupon(0) })
                }
                ex.message shouldBe "0번 쿠폰을 찾을 수 없습니다."
            }
        }

        When("이미 발급받은 쿠폰이면") {
            coEvery { repository.findByUserIdAndCouponId(any(), any()) } returns userCoupon

            Then("이미 발급된 쿠폰이라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.issueCoupon({ user }, { coupon })
                }
                ex.message shouldBe "이미 발급된 쿠폰입니다."
            }
        }

        When("아직 쿠폰 발급기간이 아니라면") {
            val invalidCoupon = Coupon(
                id = 1,
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
            coEvery { couponUseCase.getCoupon(any()) } returns invalidCoupon
            coEvery { repository.findByUserIdAndCouponId(any(), any()) } returns null

            Then("쿠폰 발급기간이 아니라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.issueCoupon({ user }, { invalidCoupon })
                }
                ex.message shouldBe "쿠폰 발급 기간이 아닙니다."
            }
        }

    }
})
