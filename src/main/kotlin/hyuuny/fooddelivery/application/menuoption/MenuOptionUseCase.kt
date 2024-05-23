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

    suspend fun updateMenuOption(id: Long, request: UpdateMenuOptionRequest) {
        val now = LocalDateTime.now()
        val menuOption = repository.findById(id) ?: throw IllegalArgumentException("존재하지 않는 메뉴옵션입니다.")
        menuOption.handle(
            UpdateMenuOptionCommand(
                name = request.name,
                price = request.price,
                updatedAt = now
            )
        )
        repository.update(menuOption)
    }

}