package hyuuny.fooddelivery.infrastructure.menugroup

import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.usingAndAwait
import org.springframework.stereotype.Component

@Component
class MenuGroupRepositoryImpl(
    private val dao: MenuGroupDao,
    private val template: R2dbcEntityTemplate,
) : MenuGroupRepository {

    override suspend fun insert(menuGroup: MenuGroup): MenuGroup = template.insert<MenuGroup>().usingAndAwait(menuGroup)

}