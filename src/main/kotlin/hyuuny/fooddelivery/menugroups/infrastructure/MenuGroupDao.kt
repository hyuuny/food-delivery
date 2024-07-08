package hyuuny.fooddelivery.menugroups.infrastructure

import hyuuny.fooddelivery.menugroups.domain.MenuGroup
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MenuGroupDao : CoroutineCrudRepository<MenuGroup, Long> {

    suspend fun findAllByStoreId(storeId: Long): List<MenuGroup>

    suspend fun findAllByStoreIdIn(storeIds: List<Long>): List<MenuGroup>

}
