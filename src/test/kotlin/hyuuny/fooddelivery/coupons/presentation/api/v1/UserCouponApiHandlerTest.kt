package hyuuny.fooddelivery.coupons.presentation.api.v1

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.application.UserCouponUseCase
import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.IssueUserCouponRequest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class UserCouponApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: UserCouponUseCase

    @MockkBean
    private lateinit var couponUseCase: CouponUseCase

    @DisplayName("회원은 갖고 있는 쿠폰 중, 사용하지 않은 쿠폰 목록을 조회할 수 있다.")
    @Test
    fun getOwnedCoupons() {
        val userId = 1L
        val couponIds = listOf(1L, 2L, 3L, 4L, 5L)
        val userCouponIds = listOf(1L, 2L, 3L, 4L, 5L)

        val now = LocalDateTime.now()
        val coupons = couponIds.mapIndexed { idx, it ->
            val upIdx = idx + 1L
            Coupon(
                id = it,
                code = "${upIdx}번 쿠폰",
                type = CouponType.STORE,
                categoryId = null,
                storeId = it + 30,
                name = "치킨 3천원 할인",
                discountAmount = 3000L,
                minimumOrderAmount = 14000,
                description = "치킨 3천원 할인 쿠폰",
                issueStartDate = now,
                issueEndDate = now.plusDays(upIdx),
                validFrom = now,
                validTo = now.plusDays(10),
                createdAt = now.plusHours(1),
            )
        }

        val userCoupons = userCouponIds.mapIndexed { idx, it ->
            val coupon = coupons[idx]
            UserCoupon(
                id = it,
                userId = userId,
                couponId = coupon.id!!,
                issuedDate = now,
                validFrom = coupon.validFrom,
                validTo = coupon.validTo,
            )
        }

        val sortedUserCoupons = userCoupons.sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(sortedUserCoupons, pageable, sortedUserCoupons.size.toLong())
        coEvery { useCase.getUserCouponByApiCondition(any(), any()) } returns page
        coEvery { couponUseCase.getAllByIds(any()) } returns coupons

        webTestClient.get().uri("/api/v1/users/$userId/coupons?sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].couponId").isEqualTo(userCoupons[4].couponId)
            .jsonPath("$.content[0].code").isEqualTo(coupons[4].code)
            .jsonPath("$.content[0].type").isEqualTo(coupons[4].type.name)
            .jsonPath("$.content[0].storeId").isEqualTo(coupons[4].storeId!!)
            .jsonPath("$.content[0].name").isEqualTo(coupons[4].name)
            .jsonPath("$.content[0].discountAmount").isEqualTo(coupons[4].discountAmount)
            .jsonPath("$.content[0].minimumOrderAmount").isEqualTo(coupons[4].minimumOrderAmount)
            .jsonPath("$.content[0].description").isEqualTo(coupons[4].description)
            .jsonPath("$.content[0].issueStartDate").exists()
            .jsonPath("$.content[0].issueEndDate").exists()
            .jsonPath("$.content[0].validFrom").exists()
            .jsonPath("$.content[0].validTo").exists()
            .jsonPath("$.content[0].available").isEqualTo(userCoupons[4].isAvailable())

            .jsonPath("$.content[1].couponId").isEqualTo(userCoupons[3].couponId)
            .jsonPath("$.content[1].code").isEqualTo(coupons[3].code)
            .jsonPath("$.content[1].type").isEqualTo(coupons[3].type.name)
            .jsonPath("$.content[1].storeId").isEqualTo(coupons[3].storeId!!)
            .jsonPath("$.content[1].name").isEqualTo(coupons[3].name)
            .jsonPath("$.content[1].discountAmount").isEqualTo(coupons[3].discountAmount)
            .jsonPath("$.content[1].minimumOrderAmount").isEqualTo(coupons[3].minimumOrderAmount)
            .jsonPath("$.content[1].description").isEqualTo(coupons[3].description)
            .jsonPath("$.content[1].issueStartDate").exists()
            .jsonPath("$.content[1].issueEndDate").exists()
            .jsonPath("$.content[1].validFrom").exists()
            .jsonPath("$.content[1].validTo").exists()
            .jsonPath("$.content[1].available").isEqualTo(userCoupons[3].isAvailable())

            .jsonPath("$.content[2].couponId").isEqualTo(userCoupons[2].couponId)
            .jsonPath("$.content[2].code").isEqualTo(coupons[2].code)
            .jsonPath("$.content[2].type").isEqualTo(coupons[2].type.name)
            .jsonPath("$.content[2].storeId").isEqualTo(coupons[2].storeId!!)
            .jsonPath("$.content[2].name").isEqualTo(coupons[2].name)
            .jsonPath("$.content[2].discountAmount").isEqualTo(coupons[2].discountAmount)
            .jsonPath("$.content[2].minimumOrderAmount").isEqualTo(coupons[2].minimumOrderAmount)
            .jsonPath("$.content[2].description").isEqualTo(coupons[2].description)
            .jsonPath("$.content[2].issueStartDate").exists()
            .jsonPath("$.content[2].issueEndDate").exists()
            .jsonPath("$.content[2].validFrom").exists()
            .jsonPath("$.content[2].validTo").exists()
            .jsonPath("$.content[2].available").isEqualTo(userCoupons[2].isAvailable())

            .jsonPath("$.content[3].couponId").isEqualTo(userCoupons[1].couponId)
            .jsonPath("$.content[3].code").isEqualTo(coupons[1].code)
            .jsonPath("$.content[3].type").isEqualTo(coupons[1].type.name)
            .jsonPath("$.content[3].storeId").isEqualTo(coupons[1].storeId!!)
            .jsonPath("$.content[3].name").isEqualTo(coupons[1].name)
            .jsonPath("$.content[3].discountAmount").isEqualTo(coupons[1].discountAmount)
            .jsonPath("$.content[3].minimumOrderAmount").isEqualTo(coupons[1].minimumOrderAmount)
            .jsonPath("$.content[3].description").isEqualTo(coupons[1].description)
            .jsonPath("$.content[3].issueStartDate").exists()
            .jsonPath("$.content[3].issueEndDate").exists()
            .jsonPath("$.content[3].validFrom").exists()
            .jsonPath("$.content[3].validTo").exists()
            .jsonPath("$.content[3].available").isEqualTo(userCoupons[1].isAvailable())

            .jsonPath("$.content[4].couponId").isEqualTo(userCoupons[0].couponId)
            .jsonPath("$.content[4].code").isEqualTo(coupons[0].code)
            .jsonPath("$.content[4].type").isEqualTo(coupons[0].type.name)
            .jsonPath("$.content[4].storeId").isEqualTo(coupons[0].storeId!!)
            .jsonPath("$.content[4].name").isEqualTo(coupons[0].name)
            .jsonPath("$.content[4].discountAmount").isEqualTo(coupons[0].discountAmount)
            .jsonPath("$.content[4].minimumOrderAmount").isEqualTo(coupons[0].minimumOrderAmount)
            .jsonPath("$.content[4].description").isEqualTo(coupons[0].description)
            .jsonPath("$.content[4].issueStartDate").exists()
            .jsonPath("$.content[4].issueEndDate").exists()
            .jsonPath("$.content[4].validFrom").exists()
            .jsonPath("$.content[4].validTo").exists()
            .jsonPath("$.content[4].available").isEqualTo(userCoupons[0].isAvailable())

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

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
            validFrom = now,
            validTo = now.plusDays(10),
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
            .jsonPath("$.validFrom").exists()
            .jsonPath("$.validTo").exists()
            .jsonPath("$.issuedDate").exists()
    }

    @DisplayName("현재 발급 가능한 쿠폰 목록이 조회된다.")
    @Test
    fun getIssuableCoupons() {
        val userId = 1L

        val now = LocalDateTime.now()
        val firstCoupon = Coupon(
            id = 1,
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = 1L,
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
            categoryId = 2L,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now,
            issueEndDate = now.plusDays(2),
            validFrom = now,
            validTo = now.plusDays(7),
            createdAt = now,
        )

        val thirdCoupon = Coupon(
            id = 3,
            code = "모래도치킨",
            type = CouponType.CATEGORY,
            categoryId = 3L,
            storeId = null,
            name = "치킨 3천원 할인",
            discountAmount = 3000L,
            minimumOrderAmount = 14000,
            description = "치킨 3천원 할인 쿠폰",
            issueStartDate = now,
            issueEndDate = now.plusDays(3),
            validFrom = now,
            validTo = now.plusDays(7),
            createdAt = now,
        )

        val yesterdayCoupon = Coupon(
            id = 4,
            code = "어제도치킨",
            type = CouponType.CATEGORY,
            categoryId = 4L,
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
        val coupons = listOf(firstCoupon, yesterdayCoupon).sortedByDescending { it.issueStartDate }

        val userCoupon = UserCoupon(
            id = 1,
            userId = userId,
            couponId = yesterdayCoupon.id!!,
            issuedDate = now,
            validFrom = yesterdayCoupon.validFrom,
            validTo = yesterdayCoupon.validTo,
        )
        coEvery { couponUseCase.getAllIssuableCoupon(any()) } returns coupons
        coEvery { useCase.getAllByUserIdAndCouponIds(any(), any()) } returns listOf(userCoupon)

        webTestClient.get().uri("/api/v1/coupons/$userId/issuable")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].couponId").isEqualTo(firstCoupon.id!!)
            .jsonPath("$[0].issued").isEqualTo(false)
            .jsonPath("$[1].couponId").isEqualTo(yesterdayCoupon.id!!)
            .jsonPath("$[1].issued").isEqualTo(true)
    }

    @DisplayName("사용 가능한 쿠폰 목록이 조회된다.")
    @Test
    fun getAvailableCoupons() {
        val userId = 1L
        val storeIds = listOf(76L, 77L, 78L, 77L, 77L)
        val couponIds = listOf(1L, 2L, 3L, 4L, 5L)
        val userCouponIds = listOf(1L, 2L, 3L, 4L, 5L)

        val now = LocalDateTime.now()
        val coupons = couponIds.mapIndexed { idx, it ->
            val upIdx = idx + 1L
            Coupon(
                id = it,
                code = "${upIdx}번 쿠폰",
                type = CouponType.STORE,
                categoryId = null,
                storeId = storeIds[idx],
                name = "치킨 할인 쿠폰",
                discountAmount = upIdx * 1000,
                minimumOrderAmount = 14000,
                description = "치킨 3천원 할인 쿠폰",
                issueStartDate = now,
                issueEndDate = now.plusDays(upIdx),
                validFrom = now,
                validTo = now.plusDays(10),
                createdAt = now.plusHours(1),
            )
        }

        val userCoupons = userCouponIds.mapIndexed { idx, it ->
            val coupon = coupons[idx]
            UserCoupon(
                id = it,
                userId = userId,
                couponId = coupon.id!!,
                issuedDate = now,
                validFrom = coupon.validFrom,
                validTo = coupon.validTo,
            )
        }
        coEvery { useCase.getAllAvailableUserCoupon(any()) } returns userCoupons
        coEvery { couponUseCase.getAllByIds(any()) } returns coupons


        val targetCategoryId = 11L
        val targetStoreId = 77L
        webTestClient.get()
            .uri("/api/v1/users/$userId/coupons/available?categoryId=$targetCategoryId&storeId=$targetStoreId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.length()").isEqualTo(5)
            .jsonPath("$[0].couponId").isEqualTo(coupons[4].id!!)
            .jsonPath("$[0].userId").isEqualTo(userId)
            .jsonPath("$[0].code").isEqualTo(coupons[4].code)
            .jsonPath("$[0].type").isEqualTo(coupons[4].type.name)
            .jsonPath("$[0].categoryId").doesNotExist()
            .jsonPath("$[0].storeId").isEqualTo(targetStoreId)
            .jsonPath("$[0].name").isEqualTo(coupons[4].name)
            .jsonPath("$[0].discountAmount").isEqualTo(coupons[4].discountAmount)
            .jsonPath("$[0].isCouponValidForOrder").isEqualTo(true)

            .jsonPath("$[1].couponId").isEqualTo(coupons[3].id!!)
            .jsonPath("$[1].userId").isEqualTo(userId)
            .jsonPath("$[1].code").isEqualTo(coupons[3].code)
            .jsonPath("$[1].type").isEqualTo(coupons[3].type.name)
            .jsonPath("$[1].categoryId").doesNotExist()
            .jsonPath("$[1].storeId").isEqualTo(targetStoreId)
            .jsonPath("$[1].name").isEqualTo(coupons[3].name)
            .jsonPath("$[1].discountAmount").isEqualTo(coupons[3].discountAmount)
            .jsonPath("$[1].isCouponValidForOrder").isEqualTo(true)

            .jsonPath("$[2].couponId").isEqualTo(coupons[1].id!!)
            .jsonPath("$[2].userId").isEqualTo(userId)
            .jsonPath("$[2].code").isEqualTo(coupons[1].code)
            .jsonPath("$[2].type").isEqualTo(coupons[1].type.name)
            .jsonPath("$[2].categoryId").doesNotExist()
            .jsonPath("$[2].storeId").isEqualTo(targetStoreId)
            .jsonPath("$[2].name").isEqualTo(coupons[1].name)
            .jsonPath("$[2].discountAmount").isEqualTo(coupons[1].discountAmount)
            .jsonPath("$[2].isCouponValidForOrder").isEqualTo(true)

            .jsonPath("$[3].couponId").isEqualTo(coupons[2].id!!)
            .jsonPath("$[3].userId").isEqualTo(userId)
            .jsonPath("$[3].code").isEqualTo(coupons[2].code)
            .jsonPath("$[3].type").isEqualTo(coupons[2].type.name)
            .jsonPath("$[3].categoryId").doesNotExist()
            .jsonPath("$[3].storeId").isEqualTo(78)
            .jsonPath("$[3].name").isEqualTo(coupons[2].name)
            .jsonPath("$[3].discountAmount").isEqualTo(coupons[2].discountAmount)
            .jsonPath("$[3].isCouponValidForOrder").isEqualTo(false)

            .jsonPath("$[4].couponId").isEqualTo(coupons[0].id!!)
            .jsonPath("$[4].userId").isEqualTo(userId)
            .jsonPath("$[4].code").isEqualTo(coupons[0].code)
            .jsonPath("$[4].type").isEqualTo(coupons[0].type.name)
            .jsonPath("$[4].categoryId").doesNotExist()
            .jsonPath("$[4].storeId").isEqualTo(76)
            .jsonPath("$[4].name").isEqualTo(coupons[0].name)
            .jsonPath("$[4].discountAmount").isEqualTo(coupons[0].discountAmount)
            .jsonPath("$[4].isCouponValidForOrder").isEqualTo(false)
    }

    @DisplayName("회원은 자신이 갖고 있는 쿠폰을 상세조회 할 수 있다.")
    @Test
    fun getCoupon() {
        val userId = 1L
        val couponId = 3L

        val now = LocalDateTime.now()
        val coupon = Coupon(
            id = couponId,
            code = "오늘도치킨",
            type = CouponType.CATEGORY,
            categoryId = 1L,
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
        coEvery { couponUseCase.getCoupon(any()) } returns coupon
        coEvery { useCase.getUserCoupon(any(), any()) } returns userCoupon

        webTestClient.get().uri("/api/v1/users/$userId/coupons/$couponId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.userId").isEqualTo(userCoupon.userId)
            .jsonPath("$.couponId").isEqualTo(userCoupon.couponId)
            .jsonPath("$.code").isEqualTo(coupon.code)
            .jsonPath("$.type").isEqualTo(coupon.type.name)
            .jsonPath("$.categoryId").isEqualTo(coupon.categoryId!!)
            .jsonPath("$.storeId").doesNotExist()
            .jsonPath("$.name").isEqualTo(coupon.name)
            .jsonPath("$.discountAmount").isEqualTo(coupon.discountAmount)
            .jsonPath("$.minimumOrderAmount").isEqualTo(coupon.minimumOrderAmount)
            .jsonPath("$.description").isEqualTo(coupon.description)
            .jsonPath("$.validFrom").exists()
            .jsonPath("$.validTo").exists()
            .jsonPath("$.used").isEqualTo(userCoupon.used)
            .jsonPath("$.usedDate").exists()
    }

}
