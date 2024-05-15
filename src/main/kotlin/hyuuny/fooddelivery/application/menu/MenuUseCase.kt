package hyuuny.fooddelivery.application.menu

import CreateMenuCommand
import CreateMenuRequest
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

}