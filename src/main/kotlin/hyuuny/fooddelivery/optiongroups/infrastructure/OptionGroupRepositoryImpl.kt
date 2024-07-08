package hyuuny.fooddelivery.optiongroups.infrastructure

import AdminOptionGroupSearchCondition
import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import kotlinx.coroutines.reactive.awaitFirstOrElse
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
import java.time.format.DateTimeFormatter

@Component
class OptionGroupRepositoryImpl(
    private val dao: OptionGroupDao,
    private val template: R2dbcEntityTemplate,
) : OptionGroupRepository {

    override suspend fun insert(optionGroup: OptionGroup): OptionGroup = dao.save(optionGroup)

    override suspend fun findById(id: Long): OptionGroup? = dao.findById(id)

    override suspend fun update(optionGroup: OptionGroup) {
        template.update<OptionGroup>()
            .matching(
                Query.query(
                    where("id").`is`(optionGroup.id!!),
                ),
            ).applyAndAwait(
                Update.update("name", optionGroup.name)
                    .set("required", optionGroup.required)
                    .set("updatedAt", optionGroup.updatedAt)
            )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun findAllOptionGroups(
        searchCondition: AdminOptionGroupSearchCondition,
        pageable: Pageable
    ): Page<OptionGroup> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)

        return template.selectAndCount<OptionGroup>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun findAllByMenuId(menuId: Long): List<OptionGroup> = dao.findAllByMenuId(menuId)

    override suspend fun bulkUpdatePriority(optionGroups: List<OptionGroup>) {
        if (optionGroups.isEmpty()) return

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val query = """
            WITH updates (id, priority, updated_at) AS (
                VALUES ${optionGroups.joinToString(", ") { "(${it.id}, ${it.priority}, '${it.updatedAt.format(formatter)}'::timestamp)" }}
            )
            UPDATE option_groups
            SET 
                priority = updates.priority, 
                updated_at = updates.updated_at
            FROM updates
            WHERE option_groups.id = updates.id
        """

        template.databaseClient.sql(query)
            .fetch()
            .rowsUpdated()
            .awaitFirstOrElse { throw RuntimeException("Batch optionGroup update failed") }
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    private fun buildCriteria(searchCondition: AdminOptionGroupSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.menuId?.let {
            criteria = criteria.and("menu_id").`is`(it)
        }

        searchCondition.name?.let {
            criteria = criteria.and("name").`is`(it)
        }

        return criteria
    }

}
