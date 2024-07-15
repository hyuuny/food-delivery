package hyuuny.fooddelivery.coupons.infrastructure

import hyuuny.fooddelivery.coupons.domain.Coupon
import hyuuny.fooddelivery.coupons.presentation.admin.v1.request.AdminCouponSearchCondition
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
class CouponRepositoryImpl(
    private val dao: CouponDao,
    private val template: R2dbcEntityTemplate,
) : CouponRepository {

    override suspend fun insert(coupon: Coupon): Coupon = dao.save(coupon)

    override suspend fun findById(id: Long): Coupon? = dao.findById(id)

    override suspend fun findByCode(code: String): Coupon? = dao.findByCode(code)

    override suspend fun findAllByIssueStartDateLessThanEqualAndIssueEndDateGreaterThanEqual(now: LocalDateTime): List<Coupon> {
        return template.select(Coupon::class.java)
            .matching(
                Query.query(
                    Criteria.where("issueStartDate").lessThanOrEquals(now)
                        .and("issueEndDate").greaterThanOrEquals(now)
                ).sort(
                    Sort.by("id").descending()
                )
            ).all()
            .collectList()
            .awaitFirstOrElse { emptyList() }
    }

    override suspend fun findAllByValidFromLessThanEqualAndValidToGreaterThanEqual(now: LocalDateTime): List<Coupon> {
        return template.select(Coupon::class.java)
            .matching(
                Query.query(
                    Criteria.where("valid_from").lessThanOrEquals(now)
                        .and("valid_to").greaterThanOrEquals(now)
                ).sort(
                    Sort.by("id").descending()
                )
            ).all()
            .collectList()
            .awaitFirstOrElse { emptyList() }
    }

    override suspend fun findAllCoupons(
        searchCondition: AdminCouponSearchCondition,
        pageable: Pageable
    ): PageImpl<Coupon> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)
        return template.selectAndCount<Coupon>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    private fun buildCriteria(searchCondition: AdminCouponSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.id?.let {
            criteria = criteria.and("id").`is`(it)
        }

        searchCondition.code?.let {
            criteria = criteria.and("code").`is`(it)
        }

        searchCondition.type?.let {
            criteria = criteria.and("type").`is`(it)
        }

        searchCondition.name?.let {
            criteria = criteria.and("name").like("%$it%")
        }

        return criteria
    }
}
