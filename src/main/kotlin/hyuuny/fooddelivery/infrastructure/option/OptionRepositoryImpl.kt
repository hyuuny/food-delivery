package hyuuny.fooddelivery.infrastructure.option

import OptionSearchCondition
import hyuuny.fooddelivery.domain.option.Option
import kotlinx.coroutines.reactive.awaitFirstOrElse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.*
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component

@Component
class OptionRepositoryImpl(
    private val dao: OptionDao,
    private val template: R2dbcEntityTemplate,
) : OptionRepository {

    override suspend fun insert(option: Option): Option =
        template.insert<Option>().usingAndAwait(option)

    override suspend fun findById(id: Long): Option? = dao.findById(id)

    override suspend fun update(option: Option) {
        template.update<Option>()
            .matching(
                Query.query(
                    where("id").`is`(option.id!!),
                ),
            ).applyAndAwait(
                Update.update("name", option.name)
                    .set("price", option.price)
                    .set("updatedAt", option.updatedAt)
            )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun findAllOptions(
        searchCondition: OptionSearchCondition,
        pageable: Pageable
    ): Page<Option> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)

        val data = template.select(Option::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirstOrElse { emptyList() }

        val total = template.select(Option::class.java)
            .matching(Query.query(criteria))
            .count()
            .awaitFirstOrElse { 0 }

        return PageImpl(data, pageable, total)
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    private fun buildCriteria(searchCondition: OptionSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.optionGroupId?.let {
            criteria = criteria.and("optionGroupId").`is`(it)
        }

        searchCondition.name?.let {
            criteria = criteria.and("name").like("%$it%")
        }

        return criteria
    }

}