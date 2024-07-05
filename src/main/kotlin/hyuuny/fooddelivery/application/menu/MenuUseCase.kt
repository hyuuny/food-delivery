package hyuuny.fooddelivery.application.menu

import AdminMenuSearchCondition
import ChangeMenuGroupCommand
import ChangeMenuStatusCommand
import ChangeMenuStatusRequest
import CreateMenuCommand
import CreateMenuRequest
import UpdateMenuCommand
import UpdateMenuRequest
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
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

    @Transactional
    suspend fun createMenu(
        request: CreateMenuRequest,
        getMenuGroup: suspend () -> MenuGroup,
    ): Menu {
        if (request.price <= 0) throw IllegalArgumentException("금액은 0이상이여야 합니다.")

        val now = LocalDateTime.now()
        val menuGroup = getMenuGroup()
        val menu = Menu.handle(
            CreateMenuCommand(
                menuGroupId = menuGroup.id!!,
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

    suspend fun getMenu(id: Long): Menu = findMenuByIdOrThrow(id)

    @Transactional
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

    @Transactional
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

    @Transactional
    suspend fun changeMenuGroup(
        id: Long,
        getMenuGroup: suspend () -> MenuGroup,
    ) {
        val now = LocalDateTime.now()
        val menuGroup = getMenuGroup()
        val menu = findMenuByIdOrThrow(id)
        menu.handle(
            ChangeMenuGroupCommand(
                menuGroupId = menuGroup.id!!,
                updatedAt = now,
            )
        )
        repository.updateMenuGroupId(menu)
    }

    @Transactional
    suspend fun deleteMenu(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 메뉴를 찾을 수 없습니다.")
        repository.delete(id)
    }

    suspend fun existsById(id: Long): Boolean = repository.existsById(id)

    suspend fun getAllByMenuGroupIds(menuGroupIds: List<Long>): List<Menu> =
        repository.findAllByMenuGroupIdIn(menuGroupIds)

    suspend fun getAllByIds(menuIds: List<Long>): List<Menu> = repository.findAllByIdIn(menuIds)

    private suspend fun findMenuByIdOrThrow(id: Long): Menu = repository.findById(id)
        ?: throw NoSuchElementException("${id}번 메뉴를 찾을 수 없습니다.")

}