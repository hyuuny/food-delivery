package hyuuny.fooddelivery.application.menuoption

import CreateMenuOptionCommand
import CreateMenuOptionRequest
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import hyuuny.fooddelivery.infrastructure.menuoption.MenuOptionRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MenuOptionUseCase(
    private val repository: MenuOptionRepository
) {

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

}