package hyuuny.fooddelivery.coupons.presentation.admin.v1

import hyuuny.fooddelivery.common.constant.CouponType
import hyuuny.fooddelivery.common.response.SimplePage
import hyuuny.fooddelivery.common.utils.extractCursorAndCount
import hyuuny.fooddelivery.common.utils.parseSort
import hyuuny.fooddelivery.coupons.application.CouponUseCase
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.AdminCouponSearchCondition
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.CreateCouponRequest
import hyuuny.fooddelivery.coupons.presentation.admin.v1.response.CouponResponse
import hyuuny.fooddelivery.coupons.presentation.admin.v1.response.CouponResponses
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class CouponHandler(
    private val useCase: CouponUseCase,
) {

    suspend fun getCoupons(request: ServerRequest): ServerResponse {
        val id = request.queryParamOrNull("id")?.toLong()
        val code = request.queryParamOrNull("code")?.takeIf { it.isNotBlank() }
        val type = request.queryParamOrNull("type")?.takeIf { it.isNotBlank() }
            ?.let { CouponType.valueOf(it.uppercase().trim()) }
        val name = request.queryParamOrNull("name")?.takeIf { it.isNotBlank() }
        val searchCondition = AdminCouponSearchCondition(
            id = id,
            code = code,
            type = type,
            name = name,
        )

        val sortParam = request.queryParamOrNull("sort")
        val sort = parseSort(sortParam)
        val (cursor, count) = extractCursorAndCount(request)

        val pageRequest = PageRequest.of(cursor, count, sort)
        val page = useCase.getCouponsByAdminCondition(searchCondition, pageRequest)
        val responses = SimplePage(page.content.map { CouponResponses.from(it) }, page)
        return ok().bodyValueAndAwait(responses)
    }

    suspend fun getCoupon(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val coupon = useCase.getCoupon(id)
        val response = CouponResponse.from(coupon)
        return ok().bodyValueAndAwait(response)
    }

    suspend fun createCoupon(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateCouponRequest>()

        val coupon = useCase.createCoupon(body)
        val response = CouponResponse.from(coupon)
        return ok().bodyValueAndAwait(response)
    }

}
