package hyuuny.fooddelivery.infrastructure.menu

import hyuuny.fooddelivery.domain.menu.Menu
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.usingAndAwait
import org.springframework.stereotype.Component

@Component
class MenuRepositoryImpl(
    private val dao: MenuDao,
    private val template: R2dbcEntityTemplate
) : MenuRepository {

    override suspend fun insert(menu: Menu): Menu = template.insert<Menu>().usingAndAwait(menu)

    override suspend fun findById(id: Long): Menu? {
        TODO("Not yet implemented")
    }

    override suspend fun update(menu: Menu): Menu {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}