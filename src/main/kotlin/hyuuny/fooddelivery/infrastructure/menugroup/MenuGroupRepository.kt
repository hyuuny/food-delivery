package hyuuny.fooddelivery.infrastructure.menugroup

import MenuGroupSearchCondition
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MenuGroupRepository {

    suspend fun insert(menuGroup: MenuGroup): MenuGroup

    suspend fun findById(id: Long): MenuGroup?

    suspend fun findAllMenuGroups(searchCondition: MenuGroupSearchCondition, pageable: Pageable): Page<MenuGroup>

}