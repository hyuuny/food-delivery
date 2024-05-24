package hyuuny.fooddelivery.infrastructure.menuoption

import MenuOptionSearchCondition
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MenuOptionRepository {

    suspend fun insert(menuOption: MenuOption): MenuOption

    suspend fun findById(id: Long): MenuOption?

    suspend fun update(menuOption: MenuOption)

    suspend fun delete(id: Long)

    suspend fun findAllMenuOptions(searchCondition: MenuOptionSearchCondition, pageable: Pageable): Page<MenuOption>

    suspend fun existsById(id: Long): Boolean

}