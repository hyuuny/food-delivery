package hyuuny.fooddelivery.infrastructure.menugroup

import MenuGroupSearchCondition
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
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
class MenuGroupRepositoryImpl(
    private val dao: MenuGroupDao,
    private val template: R2dbcEntityTemplate,
) : MenuGroupRepository {

    override suspend fun insert(menuGroup: MenuGroup): MenuGroup = template.insert<MenuGroup>().usingAndAwait(menuGroup)

    override suspend fun findById(id: Long): MenuGroup? = dao.findById(id)

    override suspend fun update(menuGroup: MenuGroup) {
        template.update<MenuGroup>()
            .matching(
                Query.query(
                    where("id").`is`(menuGroup.id!!),
                ),
            ).applyAndAwait(
                Update.update("name", menuGroup.name)
                    .set("required", menuGroup.required)
                    .set("updatedAt", menuGroup.updatedAt)
            )
    }

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun findAllMenuGroups(
        searchCondition: MenuGroupSearchCondition,
        pageable: Pageable
    ): Page<MenuGroup> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)

        val data = template.select(MenuGroup::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirstOrElse { emptyList() }

        val total = template.select(MenuGroup::class.java)
            .matching(Query.query(criteria))
            .count()
            .awaitFirstOrElse { 0 }

        return PageImpl(data, pageable, total)
    }

    override suspend fun findAllByMenuId(menuId: Long): List<MenuGroup> = dao.findAllByMenuId(menuId)

    override suspend fun bulkUpdatePriority(menuGroups: List<MenuGroup>) {
        if (menuGroups.isEmpty()) return

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val query = """
            WITH updates (id, priority, updated_at) AS (
                VALUES ${menuGroups.joinToString(", ") { "(${it.id}, ${it.priority}, '${it.updatedAt.format(formatter)}'::timestamp)" }}
            )
            UPDATE menu_group
            SET 
                priority = updates.priority, 
                updated_at = updates.updated_at
            FROM updates
            WHERE menu_group.id = updates.id
        """

        // 배치 업데이트를 실행합니다.
        template.databaseClient.sql(query)
            .fetch()
            .rowsUpdated()
            .awaitFirstOrElse { throw RuntimeException("Batch update failed") }
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    private fun buildCriteria(searchCondition: MenuGroupSearchCondition): Criteria {
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