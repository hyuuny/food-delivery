package hyuuny.fooddelivery.coupons.presentation.api.v1

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.application.UserCouponUseCase
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.IssueUserCouponRequest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class UserCouponApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: UserCouponUseCase

    @MockkBean
    private lateinit var couponUseCase: CouponUseCase

    @DisplayName("회원은 쿠폰을 발급 받을 수 있다.")
    @Test
    fun issueUserCoupon() {
        val userId = 1L
        val couponId = 3L

        val request = IssueUserCouponRequest(
            userId = userId,
            couponId = couponId
        )

        val now = LocalDateTime.now()
        val userCoupon = UserCoupon(
            id = 1,
            userId = userId,
            couponId = couponId,
            issuedDate = now,
        )
        coEvery { useCase.issueCoupon(any(), any()) } returns userCoupon

        webTestClient.post().uri("/api/v1/coupons/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(userCoupon.id!!)
            .jsonPath("$.userId").isEqualTo(userId)
            .jsonPath("$.couponId").isEqualTo(couponId)
            .jsonPath("$.used").isEqualTo(false)
            .jsonPath("$.usedDate").doesNotExist()
            .jsonPath("$.issuedDate").exists()
    }

}
