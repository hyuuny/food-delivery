package hyuuny.fooddelivery.menus.infrastructure

import hyuuny.fooddelivery.menus.domain.Menu
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MenuDao : CoroutineCrudRepository<Menu, Long> {

    suspend fun findAllByMenuGroupIdIn(menuGroupIds: List<Long>): List<Menu>

}
