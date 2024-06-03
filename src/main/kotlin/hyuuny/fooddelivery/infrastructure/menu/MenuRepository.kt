package hyuuny.fooddelivery.infrastructure.menu

import MenuSearchCondition
import hyuuny.fooddelivery.domain.menu.Menu
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MenuRepository {

    suspend fun insert(menu: Menu): Menu

    suspend fun findById(id: Long): Menu?

    suspend fun update(menu: Menu)

    suspend fun updateMenuStatus(menu: Menu)

    suspend fun delete(id: Long)

    suspend fun existsById(id: Long): Boolean

    suspend fun findAllMenus(searchCondition: MenuSearchCondition, pageable: Pageable): Page<Menu>

    suspend fun findAllByMenuGroupIdIn(menuGroupIds: List<Long>): List<Menu>

}