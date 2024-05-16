package hyuuny.fooddelivery.application.menu

import ChangeMenuStatusCommand
import ChangeMenuStatusRequest
import CreateMenuCommand
import CreateMenuRequest
import UpdateMenuCommand
import UpdateMenuRequest
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MenuUseCase(
    private val repository: MenuRepository
) {

    suspend fun createMenu(request: CreateMenuRequest): Menu {
        val now = LocalDateTime.now()
        val menu = Menu.handle(
            CreateMenuCommand(
                name = request.name,
                price = request.price,
                status = request.status,
                popularity = request.popularity,
                imageUrl = request.imageUrl,
                description = request.description,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(menu)
    }

    suspend fun getMenu(id: Long): Menu {
        return repository.findById(id)
            ?: throw IllegalStateException("${id}번 메뉴를 찾을 수 없습니다.")
    }

    suspend fun updateMenu(id: Long, request: UpdateMenuRequest) {
        val now = LocalDateTime.now()
        val menu = repository.findById(id) ?: throw IllegalStateException("${id}번 메뉴를 찾을 수 없습니다.")
        menu.handle(
            UpdateMenuCommand(
                name = request.name,
                price = request.price,
                popularity = request.popularity,
                imageUrl = request.imageUrl,
                description = request.description,
                updatedAt = now,
            )
        )
        repository.update(menu)
    }

    suspend fun changeMenuStatus(id: Long, request: ChangeMenuStatusRequest) {
        val now = LocalDateTime.now()
        val menu = repository.findById(id) ?: throw IllegalStateException("${id}번 메뉴를 찾을 수 없습니다.")
        menu.handle(
            ChangeMenuStatusCommand(
                status = request.status,
                updatedAt = now
            )
        )
        repository.updateMenuStatus(menu)
    }

}