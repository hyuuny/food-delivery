package hyuuny.fooddelivery.infrastructure.option

import OptionSearchCondition
import hyuuny.fooddelivery.domain.option.Option
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.applyAndAwait
import org.springframework.data.r2dbc.core.update
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Component
import selectAndCount

@Component
class OptionRepositoryImpl(
    private val dao: OptionDao,
    private val template: R2dbcEntityTemplate,
) : OptionRepository {

    override suspend fun insert(option: Option): Option = dao.save(option)

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

        return template.selectAndCount<Option>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    override suspend fun findAllByOptionGroupIdIn(optionGroupIds: List<Long>): List<Option> =
        dao.findAllByOptionGroupIdIn(optionGroupIds).toList()

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