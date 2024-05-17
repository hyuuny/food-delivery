package hyuuny.fooddelivery.infrastructure.menu

import hyuuny.fooddelivery.domain.menu.Menu

interface MenuRepository {

    suspend fun insert(menu: Menu): Menu

    suspend fun findById(id: Long): Menu?

    suspend fun update(menu: Menu)

    suspend fun updateMenuStatus(menu: Menu)

    suspend fun delete(id: Long)

    suspend fun existsById(id: Long): Boolean

}