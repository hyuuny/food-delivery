package hyuuny.fooddelivery.coupons.application

import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.infrastructure.CouponRepository
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.CreateCouponRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class CreateCouponUseCaseTest : BehaviorSpec({

    val repository = mockk<CouponRepository>()
    val useCase = CouponUseCase(repository)
    val verifier = mockk<CouponVerifier>()

    given("쿠폰을 등록하면서") {
        val categoryId = 1L

        val tomorrow = LocalDateTime.now().plusDays(1)
        val request = CreateCouponRequest(
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = tomorrow,
            issueEndDate = tomorrow.plusDays(7),
            validFrom = tomorrow,
            validTo = tomorrow.plusDays(7)
        )

        val now = LocalDateTime.now()
        val coupon = Coupon(
            id = 1,
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = tomorrow,
            issueEndDate = tomorrow.plusDays(7),
            validFrom = tomorrow,
            validTo = tomorrow.plusDays(7),
            createdAt = now,
        )
        coEvery { repository.findByCode(any()) } returns null
        coEvery { repository.insert(any()) } returns coupon

        `when`("올바른 정보라면") {
            val result = useCase.createCoupon(request)

            then("정상적으로 쿠폰이 등록된다.") {
                result.id.shouldNotBeNull()
                result.code shouldBe request.code
                result.type shouldBe request.type
                result.categoryId shouldBe request.categoryId
                result.storeId.shouldBeNull()
                result.name shouldBe request.name
                result.discountAmount shouldBe request.discountAmount
                result.minimumOrderAmount shouldBe request.minimumOrderAmount
                result.description shouldBe request.description
                result.issueStartDate shouldBe request.issueStartDate
                result.issueEndDate shouldBe request.issueEndDate
                result.validFrom shouldBe request.validFrom
                result.validTo shouldBe request.validTo
                result.createdAt.shouldNotBeNull()
            }
        }

        When("이미 사용중인 쿠폰 코드이면") {
            val duplicateCodeCoupon = Coupon(
                id = 1,
                code = "오늘도치킨",
                type = CouponType.CATEGORY,
                categoryId = categoryId,
                storeId = null,
                name = "치킨 3천원 할인",
                discountAmount = 3000L,
                minimumOrderAmount = 14000,
                description = "치킨 3천원 할인 쿠폰",
                issueStartDate = tomorrow,
                issueEndDate = tomorrow.plusDays(7),
                validFrom = tomorrow,
                validTo = tomorrow.plusDays(7),
                createdAt = now,
            )
            coEvery { repository.findByCode(any()) } returns duplicateCodeCoupon

            Then("사용중인 쿠폰 코드라는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(request)
                }
                ex.message shouldBe "이미 사용중인 쿠폰 코드입니다. code: ${request.code}"
            }
        }

        When("카테고리 할인 쿠폰에 카테고리 아이디가 없으면") {
            val invalidCodeRequest = request.copy(type = CouponType.CATEGORY, categoryId = null)
            coEvery { repository.findByCode(any()) } returns null
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("카테고리 쿠폰은 카테고리 아이디가 필수 값입니다.")

            Then("카테고리 아이디는 필수 값이라는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "카테고리 쿠폰은 카테고리 아이디가 필수 값입니다."
            }
        }

        When("매장 할인 쿠폰에 매장 아이디가 없으면") {
            val invalidCodeRequest = request.copy(type = CouponType.STORE, storeId = null)
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("매장 쿠폰은 매장 아이디가 필수 값입니다.")

            Then("매장 아이디는 필수 값이라는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "매장 쿠폰은 매장 아이디가 필수 값입니다."
            }
        }

        When("쿠폰 코드가 4자 이하이면") {
            val invalidCodeRequest = request.copy(code = "방가")
            coEvery { repository.findByCode(any()) } returns null
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("쿠폰 코드는 4자 이상이어야 합니다. code: ${invalidCodeRequest.code}")

            Then("쿠폰 코드는 4자 이상이어야 한다는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "쿠폰 코드는 4자 이상이여야 합니다. code: ${invalidCodeRequest.code}"
            }
        }

        When("할인 금액이 1000원 미만이면") {
            val invalidCodeRequest = request.copy(discountAmount = 500L)
            coEvery { repository.findByCode(any()) } returns null
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("할인 금액은 1000원 이상이여야 합니다. discountAmount: ${invalidCodeRequest.discountAmount}")

            Then("할인 금액은 1000원 이상이여야 한다는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "할인 금액은 1000원 이상이여야 합니다. discountAmount: ${invalidCodeRequest.discountAmount}"
            }
        }

        When("쿠폰 발급 시작일이 현재 시간보다 이전이면") {
            val invalidCodeRequest = request.copy(issueStartDate = now.minusDays(1))
            coEvery { repository.findByCode(any()) } returns null
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("쿠폰 발급 시작일은 현재 시간 이후여야 합니다.")

            Then("쿠폰 발급 시작일은 현재 시간 이후여야 한다는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "쿠폰 발급 시작일은 현재 시간 이후여야 합니다."
            }
        }

        When("쿠폰 발급 시작일이 쿠폰 발급 종료일보다 이전이면") {
            val invalidCodeRequest = request.copy(issueEndDate = now.minusDays(30))
            coEvery { repository.findByCode(any()) } returns null
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("쿠폰 발급 시작일은 종료일 이전이여야 합니다.")

            Then("쿠폰 발급 시작일은 종료일 이전이여야 한다는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "쿠폰 발급 시작일은 종료일 이전이여야 합니다."
            }
        }

        When("쿠폰 사용 시작일이 현재 시간보다 이전이면") {
            val invalidCodeRequest = request.copy(validFrom = now.minusDays(1))
            coEvery { repository.findByCode(any()) } returns null
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("쿠폰 사용 시작일은 현재 시간 이후여야 합니다.")

            Then("쿠폰 사용 시작일은 현재 시간 이후여야 한다는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "쿠폰 사용 시작일은 현재 시간 이후여야 합니다."
            }
        }

        When("쿠폰 사용 시작일이 쿠폰 사용 종료일보다 이전이면") {
            val invalidCodeRequest = request.copy(validTo = now.minusDays(30))
            coEvery { repository.findByCode(any()) } returns null
            coEvery { verifier.verify(any()) } throws IllegalArgumentException("쿠폰 사용 시작일은 종료일 이전이여야 합니다.")

            Then("쿠폰 사용 시작일은 종료일보다 이전이여야 한다는 메세지를 반환한다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createCoupon(invalidCodeRequest)
                }
                ex.message shouldBe "쿠폰 사용 시작일은 종료일 이전이여야 합니다."
            }
        }

    }
})
