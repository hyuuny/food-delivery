package hyuuny.fooddelivery.infrastructure.menuoption

import hyuuny.fooddelivery.domain.menuoption.MenuOption
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.usingAndAwait
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

}