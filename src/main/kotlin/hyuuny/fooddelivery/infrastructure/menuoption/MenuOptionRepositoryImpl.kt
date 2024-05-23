package hyuuny.fooddelivery.infrastructure.menuoption

import MenuOptionSearchCondition
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import kotlinx.coroutines.reactive.awaitFirstOrElse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.usingAndAwait
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component

@Component
class MenuOptionRepositoryImpl(
    private val dao: MenuOptionDao,
    private val template: R2dbcEntityTemplate,
) : MenuOptionRepository {

    override suspend fun insert(menuOption: MenuOption): MenuOption =
        template.insert<MenuOption>().usingAndAwait(menuOption)

    override suspend fun findById(id: Long): MenuOption? {
        TODO("Not yet implemented")
    }

    override suspend fun update(menuOption: MenuOption) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun findAllMenuOptions(
        searchCondition: MenuOptionSearchCondition,
        pageable: Pageable
    ): Page<MenuOption> {
        val criteria = buildCriteria(searchCondition)
        val query = Query.query(criteria).with(pageable)

        val data = template.select(MenuOption::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirstOrElse { emptyList() }

        val total = template.select(MenuOption::class.java)
            .matching(Query.query(criteria))
            .count()
            .awaitFirstOrElse { 0 }

        return PageImpl(data, pageable, total)
    }

    private fun buildCriteria(searchCondition: MenuOptionSearchCondition): Criteria {
        var criteria = Criteria.empty()

        searchCondition.menuGroupId?.let {
            criteria = criteria.and("menuGroupId").`is`(it)
        }

        searchCondition.name?.let {
            criteria = criteria.and("name").like("%$it%")
        }

        return criteria
    }

}