package hyuuny.fooddelivery.application.menu

import AdminMenuSearchCondition
import ChangeMenuStatusCommand
import ChangeMenuStatusRequest
import CreateMenuCommand
import CreateMenuRequest
import UpdateMenuCommand
import UpdateMenuRequest
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MenuUseCase(
    private val repository: MenuRepository
) {

    suspend fun getMenusByAdminCondition(
        searchCondition: AdminMenuSearchCondition,
        pageable: Pageable
    ): PageImpl<Menu> {
        val page = repository.findAllMenus(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun createMenu(request: CreateMenuRequest): Menu {
        if (request.price <= 0) throw IllegalArgumentException("금액은 0이상이여야 합니다.")

        val now = LocalDateTime.now()
        val menu = Menu.handle(
            CreateMenuCommand(
                menuGroupId = request.menuGroupId,
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
        return findMenuByIdOrThrow(id)
    }

    suspend fun updateMenu(id: Long, request: UpdateMenuRequest) {
        if (request.price <= 0) throw IllegalArgumentException("금액은 0이상이여야 합니다.")

        val now = LocalDateTime.now()
        val menu = findMenuByIdOrThrow(id)
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
        val menu = findMenuByIdOrThrow(id)
        menu.handle(
            ChangeMenuStatusCommand(
                status = request.status,
                updatedAt = now
            )
        )
        repository.updateMenuStatus(menu)
    }

    suspend fun deleteMenu(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 메뉴를 찾을 수 없습니다.")
        repository.delete(id)
    }

    suspend fun existById(id: Long): Boolean = repository.existsById(id)

    suspend fun getAllByMenuGroupIds(menuGroupIds: List<Long>): List<Menu> =
        repository.findAllByMenuGroupIdIn(menuGroupIds)

    suspend fun getAllByIds(menuIds: List<Long>): List<Menu> = repository.findAllByIdIn(menuIds)

    private suspend fun findMenuByIdOrThrow(id: Long): Menu = repository.findById(id)
        ?: throw NoSuchElementException("${id}번 메뉴를 찾을 수 없습니다.")

}