package hyuuny.fooddelivery.coupons.presentation.api.v1

import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.application.UserCouponUseCase
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.ApiCouponSearchCondition
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.IssueUserCouponRequest
import hyuuny.fooddelivery.coupons.presentation.api.v1.response.UserCouponResponse
import hyuuny.fooddelivery.coupons.presentation.api.v1.response.UserWithCouponResponse
import hyuuny.fooddelivery.users.application.UserUseCase
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Component
class UserCouponApiHandler(
    private val useCase: UserCouponUseCase,
    private val userUseCase: UserUseCase,
    private val couponUseCase: CouponUseCase,
    private val responseMapper: CouponResponseMapper,
) {

    suspend fun getOwnedCoupons(request: ServerRequest): ServerResponse {
        val now = LocalDateTime.now()
        val userId = request.pathVariable("userId").toLong()
        val searchCondition = ApiCouponSearchCondition(
            userId = userId,
            now = now,
            used = false,
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getUserCouponByApiCondition(searchCondition, pageRequest)
        val couponResponses = responseMapper.mapToOwnedUserCouponResponses(page.content)
        val responses = SimplePage(couponResponses, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun issueUserCoupon(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<IssueUserCouponRequest>()

        val userCoupon = useCase.issueCoupon(
            getCoupon = { couponUseCase.getCoupon(body.couponId) },
            getUser = { userUseCase.getUser(body.userId) }
        )
        val response = UserCouponResponse.from(userCoupon)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun getIssuableCoupons(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()

        val now = LocalDateTime.now()
        val issuableCoupons = couponUseCase.getAllIssuableCoupon(now).sortedByDescending { it.issueStartDate }
        val userCoupons = useCase.getAllByUserIdAndCouponIds(userId, issuableCoupons.mapNotNull { it.id })
        val responses = responseMapper.mapToIssuableCouponResponses(issuableCoupons, userCoupons)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getAvailableCoupons(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").toLong()

        val categoryId = request.queryParamOrThrow("categoryId")
        val storeId = request.queryParamOrThrow("storeId")

        val userCoupons = useCase.getAllAvailableUserCoupon(userId)
        val responses = responseMapper.mapToAvailableCouponResponses(categoryId, storeId, userCoupons)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getCoupon(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val userId = request.pathVariable("userId").toLong()

        val coupon = couponUseCase.getCoupon(id)
        val userCoupon = useCase.getUserCoupon(coupon.id!!) { userUseCase.getUser(userId) }
        val response = UserWithCouponResponse.from(userCoupon, coupon)
        return ok().bodyValueAndAwait(response)
    }

    private fun ServerRequest.queryParamOrThrow(name: String): Long = queryParam(name)
        .map { it.toLong() }.orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }


}
