package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.UserCoupon
import hyuuny.fooddelivery.coupons.presentation.api.v1.request.ApiCouponSearchCondition
import kotlinx.coroutines.reactive.awaitFirstOrElse
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import selectAndCount
import java.time.LocalDateTime

@Component
class UserCouponRepositoryImpl(
    private val dao: UserCouponDao,
    private val template: R2dbcEntityTemplate,
) : UserCouponRepository {

    override suspend fun insert(userCoupon: UserCoupon): UserCoupon = dao.save(userCoupon)

    override suspend fun findByUserIdAndCouponId(userId: Long, couponId: Long): UserCoupon? =
        dao.findByUserIdAndCouponId(userId, couponId)

    override suspend fun findAllByUserIdAndCouponIdIn(userId: Long, couponIds: List<Long>): List<UserCoupon> =
        dao.findAllByUserIdAndCouponIdIn(userId, couponIds)

    override suspend fun findAllByUserIdAndUsedFalse(userId: Long, now: LocalDateTime): List<UserCoupon> {
        return template.select(UserCoupon::class.java)
            .matching(
                Query.query(
                    Criteria.where("userId").`is`(userId)
                        .and("validFrom").lessThanOrEquals(now)
                        .and("validTo").greaterThanOrEquals(now)
                        .and("used").isFalse
                ).sort(
                    Sort.by("id").descending()
                )
            ).all()
            .collectList()
            .awaitFirstOrElse { emptyList() }
    }

    override suspend fun existsByUserIdAndCouponId(userId: Long, couponId: Long): Boolean =
        dao.existsByUserIdAndCouponId(userId, couponId)

    override suspend fun findAllUserCoupons(
        searchCondition: ApiCouponSearchCondition,
        pageable: Pageable
    ): PageImpl<UserCoupon> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<UserCoupon>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    private fun buildCriteria(searchCondition: ApiCouponSearchCondition): Criteria {
        var criteria = Criteria.empty()

        criteria = criteria.and("userId").`is`(searchCondition.userId)
        criteria = criteria.and("validFrom").lessThanOrEquals(searchCondition.now).and("validTo")
            .greaterThanOrEquals(searchCondition.now)
        criteria = criteria.and("used").`is`(searchCondition.used)

        return criteria
    }
}
