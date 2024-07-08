package hyuuny.fooddelivery.menus.infrastructure

import AdminMenuSearchCondition
import hyuuny.fooddelivery.menus.domain.Menu
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MenuRepository {

    suspend fun insert(menu: Menu): Menu

    suspend fun findById(id: Long): Menu?

    suspend fun update(menu: Menu)

    suspend fun updateMenuStatus(menu: Menu)

    suspend fun updateMenuGroupId(menu: Menu)

    suspend fun delete(id: Long)

    suspend fun existsById(id: Long): Boolean

    suspend fun findAllMenus(searchCondition: AdminMenuSearchCondition, pageable: Pageable): Page<Menu>

    suspend fun findAllByMenuGroupIdIn(menuGroupIds: List<Long>): List<Menu>

    suspend fun findAllByIdIn(ids: List<Long>): List<Menu>

}
