package hyuuny.fooddelivery.infrastructure.menugroup

import MenuGroupSearchCondition
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

interface MenuGroupRepository {

    suspend fun insert(menuGroup: MenuGroup): MenuGroup

    suspend fun findById(id: Long): MenuGroup?

    suspend fun update(menuGroup: MenuGroup)

    suspend fun delete(id: Long)

    suspend fun findAllMenuGroups(searchCondition: MenuGroupSearchCondition, pageable: Pageable): PageImpl<MenuGroup>

    suspend fun findAllByStoreId(storeId: Long): List<MenuGroup>

    suspend fun bulkUpdatePriority(menuGroups: List<MenuGroup>)

    suspend fun existsById(id: Long): Boolean

    suspend fun findAllByStoreIds(storeIds: List<Long>): List<MenuGroup>

}
