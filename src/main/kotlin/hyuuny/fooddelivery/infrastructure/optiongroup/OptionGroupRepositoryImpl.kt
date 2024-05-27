package hyuuny.fooddelivery.infrastructure.optiongroup

import OptionGroupSearchCondition
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
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
import java.time.format.DateTimeFormatter

@Component
class OptionGroupRepositoryImpl(
    private val dao: OptionGroupDao,
    private val template: R2dbcEntityTemplate,
) : OptionGroupRepository {

    override suspend fun insert(optionGroup: OptionGroup): OptionGroup = template.insert<OptionGroup>().usingAndAwait(optionGroup)

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
        searchCondition: OptionGroupSearchCondition,
        pageable: Pageable
    ): Page<OptionGroup> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)

        val data = template.select(OptionGroup::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirstOrElse { emptyList() }

        val total = template.select(OptionGroup::class.java)
            .matching(Query.query(criteria))
            .count()
            .awaitFirstOrElse { 0 }

        return PageImpl(data, pageable, total)
    }

    override suspend fun findAllByMenuId(menuId: Long): List<OptionGroup> = dao.findAllByMenuId(menuId)

    override suspend fun bulkUpdatePriority(optionGroups: List<OptionGroup>) {
        if (optionGroups.isEmpty()) return

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val query = """
            WITH updates (id, priority, updated_at) AS (
                VALUES ${optionGroups.joinToString(", ") { "(${it.id}, ${it.priority}, '${it.updatedAt.format(formatter)}'::timestamp)" }}
            )
            UPDATE option_group
            SET 
                priority = updates.priority, 
                updated_at = updates.updated_at
            FROM updates
            WHERE option_group.id = updates.id
        """

        // 배치 업데이트를 실행합니다.
        template.databaseClient.sql(query)
            .fetch()
            .rowsUpdated()
            .awaitFirstOrElse { throw RuntimeException("Batch update failed") }
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    private fun buildCriteria(searchCondition: OptionGroupSearchCondition): Criteria {
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