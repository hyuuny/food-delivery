package hyuuny.fooddelivery.infrastructure.menugroup

import hyuuny.fooddelivery.domain.menugroup.MenuGroup

interface MenuGroupRepository {

    suspend fun insert(menuGroup: MenuGroup): MenuGroup

    suspend fun findById(id: Long): MenuGroup?

}