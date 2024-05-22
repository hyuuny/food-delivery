package hyuuny.fooddelivery.infrastructure.menuoption

import hyuuny.fooddelivery.domain.menuoption.MenuOption
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MenuOptionDao : CoroutineCrudRepository<MenuOption, Long> {
}