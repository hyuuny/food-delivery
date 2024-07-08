package hyuuny.fooddelivery.menugroups.infrastructure

import AdminMenuGroupSearchCondition
import hyuuny.fooddelivery.menugroups.domain.MenuGroup
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface MenuGroupRepository {

    suspend fun insert(menuGroup: MenuGroup): MenuGroup

    suspend fun findById(id: Long): MenuGroup?

    suspend fun update(menuGroup: MenuGroup)

    suspend fun delete(id: Long)

    suspend fun findAllMenuGroups(searchCondition: AdminMenuGroupSearchCondition, pageable: Pageable): PageImpl<MenuGroup>

    suspend fun findAllByStoreId(storeId: Long): List<MenuGroup>

    suspend fun bulkUpdatePriority(menuGroups: List<MenuGroup>)

    suspend fun existsById(id: Long): Boolean

    suspend fun findAllByStoreIdIn(storeIds: List<Long>): List<MenuGroup>

}
