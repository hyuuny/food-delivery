package hyuuny.fooddelivery.infrastructure.menugroup

import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MenuGroupDao : CoroutineCrudRepository<MenuGroup, Long> {

    suspend fun findAllByMenuId(menuId: Long): List<MenuGroup>

}