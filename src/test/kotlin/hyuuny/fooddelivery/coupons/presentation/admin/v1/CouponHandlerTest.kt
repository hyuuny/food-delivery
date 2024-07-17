package hyuuny.fooddelivery.coupons.presentation.admin.v1

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.CreateCouponRequest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class CouponHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: CouponUseCase

    @DisplayName("쿠폰을 등록할 수 있다.")
    @Test
    fun createCoupon() {
        val categoryId = 1L

        val now = LocalDateTime.now()
        val request = CreateCouponRequest(
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now.plusDays(1),
            issueEndDate = now.plusDays(7),
            validFrom = now.plusDays(1),
            validTo = now.plusDays(7)
        )

        val coupon = generateCoupon(request)
        coEvery { useCase.createCoupon(any()) } returns coupon

        webTestClient.post().uri("/admin/v1/coupons")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(coupon.id!!)
            .jsonPath("$.code").isEqualTo(coupon.code)
            .jsonPath("$.type").isEqualTo(coupon.type.name)
            .jsonPath("$.name").isEqualTo(coupon.name)
            .jsonPath("$.discountAmount").isEqualTo(coupon.discountAmount)
            .jsonPath("$.minimumOrderAmount").isEqualTo(coupon.minimumOrderAmount)
            .jsonPath("$.description").isEqualTo(coupon.description)
            .jsonPath("$.issueStartDate").exists()
            .jsonPath("$.issueEndDate").exists()
            .jsonPath("$.validFrom").exists()
            .jsonPath("$.validTo").exists()
    }

    @DisplayName("쿠폰을 조회 및 검색할 수 있다.")
    @Test
    fun getCoupons() {
        val categoryId = 1L
        val couponIds = listOf(1, 2, 3, 4, 5)

        val now = LocalDateTime.now()
        val coupons = couponIds.mapIndexed { idx, it ->
            val upIdx = idx + 1L
            Coupon(
                id = it.toLong(),
                code = "${upIdx}번 쿠폰",
                type = CouponType.CATEGORY,
                categoryId = categoryId,
                storeId = null,
                name = "치킨 3천원 할인",
                discountAmount = 3000L,
                minimumOrderAmount = 14000,
                description = "치킨 3천원 할인 쿠폰",
                issueStartDate = now.plusDays(1),
                issueEndDate = now.plusDays(upIdx),
                validFrom = now.plusDays(1),
                validTo = now.plusDays(upIdx),
                createdAt = now.plusHours(1),
            )
        }
        val sortedCoupons = coupons.sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(sortedCoupons, pageable, sortedCoupons.size.toLong())
        coEvery { useCase.getCouponsByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/coupons?sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].id").isEqualTo(coupons[4].id!!)
            .jsonPath("$.content[0].code").isEqualTo(coupons[4].code)
            .jsonPath("$.content[0].type").isEqualTo(coupons[4].type.name)
            .jsonPath("$.content[0].categoryId").isEqualTo(coupons[4].categoryId!!)
            .jsonPath("$.content[0].storeId").doesNotExist()
            .jsonPath("$.content[0].discountAmount").isEqualTo(coupons[4].discountAmount)
            .jsonPath("$.content[0].issueStartDate").exists()
            .jsonPath("$.content[0].issueEndDate").exists()
            .jsonPath("$.content[0].validFrom").exists()
            .jsonPath("$.content[0].validTo").exists()

            .jsonPath("$.content[1].id").isEqualTo(coupons[3].id!!)
            .jsonPath("$.content[1].code").isEqualTo(coupons[3].code)
            .jsonPath("$.content[1].type").isEqualTo(coupons[3].type.name)
            .jsonPath("$.content[1].categoryId").isEqualTo(coupons[3].categoryId!!)
            .jsonPath("$.content[1].storeId").doesNotExist()
            .jsonPath("$.content[1].name").isEqualTo(coupons[3].name)
            .jsonPath("$.content[1].discountAmount").isEqualTo(coupons[3].discountAmount)
            .jsonPath("$.content[1].issueStartDate").exists()
            .jsonPath("$.content[1].issueEndDate").exists()
            .jsonPath("$.content[1].validFrom").exists()
            .jsonPath("$.content[1].validTo").exists()

            .jsonPath("$.content[2].id").isEqualTo(coupons[2].id!!)
            .jsonPath("$.content[2].code").isEqualTo(coupons[2].code)
            .jsonPath("$.content[2].type").isEqualTo(coupons[2].type.name)
            .jsonPath("$.content[2].categoryId").isEqualTo(coupons[2].categoryId!!)
            .jsonPath("$.content[2].storeId").doesNotExist()
            .jsonPath("$.content[2].name").isEqualTo(coupons[2].name)
            .jsonPath("$.content[2].discountAmount").isEqualTo(coupons[2].discountAmount)
            .jsonPath("$.content[2].issueStartDate").exists()
            .jsonPath("$.content[2].issueEndDate").exists()
            .jsonPath("$.content[2].validFrom").exists()
            .jsonPath("$.content[2].validTo").exists()

            .jsonPath("$.content[3].id").isEqualTo(coupons[1].id!!)
            .jsonPath("$.content[3].code").isEqualTo(coupons[1].code)
            .jsonPath("$.content[3].type").isEqualTo(coupons[1].type.name)
            .jsonPath("$.content[3].categoryId").isEqualTo(coupons[1].categoryId!!)
            .jsonPath("$.content[3].storeId").doesNotExist()
            .jsonPath("$.content[3].name").isEqualTo(coupons[1].name)
            .jsonPath("$.content[3].discountAmount").isEqualTo(coupons[1].discountAmount)
            .jsonPath("$.content[3].issueStartDate").exists()
            .jsonPath("$.content[3].issueEndDate").exists()
            .jsonPath("$.content[3].validFrom").exists()
            .jsonPath("$.content[3].validTo").exists()

            .jsonPath("$.content[4].id").isEqualTo(coupons[0].id!!)
            .jsonPath("$.content[4].code").isEqualTo(coupons[0].code)
            .jsonPath("$.content[4].type").isEqualTo(coupons[0].type.name)
            .jsonPath("$.content[4].categoryId").isEqualTo(coupons[0].categoryId!!)
            .jsonPath("$.content[4].storeId").doesNotExist()
            .jsonPath("$.content[4].name").isEqualTo(coupons[0].name)
            .jsonPath("$.content[4].discountAmount").isEqualTo(coupons[0].discountAmount)
            .jsonPath("$.content[4].issueStartDate").exists()
            .jsonPath("$.content[4].issueEndDate").exists()
            .jsonPath("$.content[4].validFrom").exists()
            .jsonPath("$.content[4].validTo").exists()

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

    @DisplayName("쿠폰을 상세조회 할 수 있다.")
    @Test
    fun getCoupon() {
        val categoryId = 1L

        val now = LocalDateTime.now()
        val request = CreateCouponRequest(
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = categoryId,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now.plusDays(1),
            issueEndDate = now.plusDays(7),
            validFrom = now.plusDays(1),
            validTo = now.plusDays(7)
        )
        val coupon = generateCoupon(request)
        coEvery { useCase.getCoupon(any()) } returns coupon

        webTestClient.get().uri("/admin/v1/coupons/${coupon.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(coupon.id!!)
            .jsonPath("$.code").isEqualTo(coupon.code)
            .jsonPath("$.type").isEqualTo(coupon.type.name)
            .jsonPath("$.categoryId").isEqualTo(coupon.categoryId!!)
            .jsonPath("$.storeId").doesNotExist()
            .jsonPath("$.name").isEqualTo(coupon.name)
            .jsonPath("$.discountAmount").isEqualTo(coupon.discountAmount)
            .jsonPath("$.minimumOrderAmount").isEqualTo(coupon.minimumOrderAmount)
            .jsonPath("$.description").isEqualTo(coupon.description)
            .jsonPath("$.issueStartDate").exists()
            .jsonPath("$.issueEndDate").exists()
            .jsonPath("$.validFrom").exists()
            .jsonPath("$.validTo").exists()
    }

    private fun generateCoupon(request: CreateCouponRequest): Coupon {
        val now = LocalDateTime.now()
        return Coupon(
            id = 1,
            code = request.code,
            type = request.type,
            categoryId = request.categoryId,
            storeId = request.storeId,
            name = request.name,
            discountAmount = request.discountAmount,
            minimumOrderAmount = request.minimumOrderAmount,
            description = request.description,
            issueStartDate = request.issueStartDate,
            issueEndDate = request.issueEndDate,
            validFrom = request.validFrom,
            validTo = request.validTo,
            createdAt = now,
        )

    }
}
