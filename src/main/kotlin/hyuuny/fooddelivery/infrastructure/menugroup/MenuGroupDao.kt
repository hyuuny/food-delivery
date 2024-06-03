package hyuuny.fooddelivery.infrastructure.menugroup

import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MenuGroupDao : CoroutineCrudRepository<MenuGroup, Long> {

    suspend fun findAllByStoreId(storeId: Long): List<MenuGroup>

    suspend fun findAllByStoreIdIn(storeIds: List<Long>): List<MenuGroup>

}
