package hyuuny.fooddelivery.infrastructure.menu

import hyuuny.fooddelivery.domain.menu.Menu
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MenuDao : CoroutineCrudRepository<Menu, Long> {

    suspend fun findAllByMenuGroupIdIn(menuGroupIds: List<Long>): List<Menu>

}