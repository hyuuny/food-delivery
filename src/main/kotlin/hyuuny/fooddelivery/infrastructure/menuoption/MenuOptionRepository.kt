package hyuuny.fooddelivery.infrastructure.menuoption

import hyuuny.fooddelivery.domain.menuoption.MenuOption

interface MenuOptionRepository {

    suspend fun insert(menuOption: MenuOption): MenuOption

    suspend fun findById(id: Long): MenuOption?

    suspend fun update(menuOption: MenuOption)

    suspend fun delete(id: Long)

}