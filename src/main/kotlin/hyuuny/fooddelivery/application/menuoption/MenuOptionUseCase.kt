package hyuuny.fooddelivery.application.menuoption

import CreateMenuOptionCommand
import CreateMenuOptionRequest
import MenuOptionSearchCondition
import UpdateMenuOptionCommand
import UpdateMenuOptionRequest
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import hyuuny.fooddelivery.infrastructure.menuoption.MenuOptionRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MenuOptionUseCase(
    private val repository: MenuOptionRepository
) {

    suspend fun getMenuOptions(searchCondition: MenuOptionSearchCondition, pageable: Pageable): PageImpl<MenuOption> {
        val page = repository.findAllMenuOptions(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun createMenuOption(request: CreateMenuOptionRequest): MenuOption {
        val now = LocalDateTime.now()
        val menuOption = MenuOption.handle(
            CreateMenuOptionCommand(
                menuGroupId = request.menuGroupId,
                name = request.name,
                price = request.price,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(menuOption)
    }

    suspend fun getMenuOption(id: Long): MenuOption {
        return findMenuOptionByIdOrThrow(id)
    }

    suspend fun updateMenuOption(id: Long, request: UpdateMenuOptionRequest) {
        val now = LocalDateTime.now()
        val menuOption = findMenuOptionByIdOrThrow(id)
        menuOption.handle(
            UpdateMenuOptionCommand(
                name = request.name,
                price = request.price,
                updatedAt = now
            )
        )
        repository.update(menuOption)
    }

    private suspend fun findMenuOptionByIdOrThrow(id: Long): MenuOption = repository.findById(id)
        ?: throw NoSuchElementException("메뉴옵션을 찾을 수 없습니다.")

    suspend fun deleteMenuOption(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("메뉴옵션을 찾을 수 없습니다.")
        repository.delete(id)
    }

}