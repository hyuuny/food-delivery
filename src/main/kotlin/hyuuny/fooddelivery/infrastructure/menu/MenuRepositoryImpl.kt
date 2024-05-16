package hyuuny.fooddelivery.infrastructure.menu

import hyuuny.fooddelivery.domain.menu.Menu
import org.springframework.data.r2dbc.core.*
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.data.relational.core.query.isEqual
import org.springframework.stereotype.Component

@Component
class MenuRepositoryImpl(
    private val dao: MenuDao,
    private val template: R2dbcEntityTemplate
) : MenuRepository {

    override suspend fun insert(menu: Menu): Menu = template.insert<Menu>().usingAndAwait(menu)

    override suspend fun findById(id: Long): Menu? = dao.findById(id)

    override suspend fun update(menu: Menu) {
        template.update<Menu>()
            .matching(
                Query.query(
                    where("id") isEqual (menu.id!!),
                ),
            ).applyAndAwait(
                Update.update("name", menu.name)
                    .set("price", menu.price.value)
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
        TODO("Not yet implemented")
    }
}