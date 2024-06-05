package hyuuny.fooddelivery.infrastructure.menu

import AdminMenuSearchCondition
import hyuuny.fooddelivery.domain.menu.Menu
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
import org.springframework.data.relational.core.query.isEqual
import org.springframework.stereotype.Component
import selectAndCount

@Component
class MenuRepositoryImpl(
    private val dao: MenuDao,
    private val template: R2dbcEntityTemplate
) : MenuRepository {

    override suspend fun insert(menu: Menu): Menu = dao.save(menu)

    override suspend fun findById(id: Long): Menu? = dao.findById(id)

    override suspend fun update(menu: Menu) {
        template.update<Menu>()
            .matching(
                Query.query(
                    where("id") isEqual (menu.id!!),
                ),
            ).applyAndAwait(
                Update.update("name", menu.name)
                    .set("price", menu.price)
                    .set("popularity", menu.popularity)
                    .set("imageUrl", menu.imageUrl)
                    .set("description", menu.description)
                    .set("updatedAt", menu.updatedAt)
            )
    }

    override suspend fun updateMenuStatus(menu: Menu) {
        template.update<Menu>()
            .matching(
                Query.query(
                    where("id") isEqual (menu.id!!),
                ),
            ).applyAndAwait(
                Update.update("status", menu.status)
                    .set("updatedAt", menu.updatedAt)
            )
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun existsById(id: Long): Boolean = dao.existsById(id)

    override suspend fun findAllMenus(searchCondition: AdminMenuSearchCondition, pageable: Pageable): Page<Menu> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)

        return template.selectAndCount<Menu>(query, criteria).let { (data, total) ->
            PageImpl(data, pageable, total)
        }
    }

    override suspend fun findAllByMenuGroupIdIn(menuGroupIds: List<Long>): List<Menu> =
        dao.findAllByMenuGroupIdIn(menuGroupIds)

    private fun buildCriteria(condition: AdminMenuSearchCondition): Criteria {
        var criteria = Criteria.empty()

        condition.name?.let {
            criteria = criteria.and("name").like("%$it%")
        }
        condition.status?.let {
            criteria = criteria.and("status").`is`(it.name)
        }
        condition.popularity?.let {
            criteria = criteria.and("popularity").`is`(it)
        }

        return criteria
    }
}