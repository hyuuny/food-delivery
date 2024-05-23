package hyuuny.fooddelivery.application.menugroup

import CreateMenuGroupCommand
import CreateMenuGroupRequest
import MenuGroupSearchCondition
import ReOrderMenuGroupCommand
import ReorderMenuGroupRequests
import UpdateMenuGroupCommand
import UpdateMenuGroupRequest
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MenuGroupUseCase(
    private val repository: MenuGroupRepository
) {

    suspend fun getMenuGroups(searchCondition: MenuGroupSearchCondition, pageable: Pageable): PageImpl<MenuGroup> {
        val page = repository.findAllMenuGroups(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun createMenuGroup(request: CreateMenuGroupRequest): MenuGroup {
        val now = LocalDateTime.now()
        val menuGroup = MenuGroup.handle(
            CreateMenuGroupCommand(
                menuId = request.menuId,
                name = request.name,
                required = request.required,
                priority = request.priority,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(menuGroup)
    }

    suspend fun getMenuGroup(id: Long): MenuGroup {
        return findMenuGroupByIdOrThrow(id)
    }

    suspend fun updateMenuGroup(id: Long, request: UpdateMenuGroupRequest) {
        val now = LocalDateTime.now()
        val menuGroup = findMenuGroupByIdOrThrow(id)
        menuGroup.handle(
            UpdateMenuGroupCommand(
                name = request.name,
                required = request.required,
                updatedAt = now,
            )
        )
        repository.update(menuGroup)
    }

    suspend fun reOrderMenuGroups(menuId: Long, request: ReorderMenuGroupRequests) {
        val now = LocalDateTime.now()
        val menuGroups = repository.findAllByMenuId(menuId)

        if (menuGroups.size != request.reOrderedMenuGroups.size) throw IllegalStateException("메뉴그룹의 개수가 일치하지 않습니다.")

        val menuGroupMap = menuGroups.associateBy { it.id }
        request.reOrderedMenuGroups.forEach {
            val menuGroup = menuGroupMap[it.menuGroupId] ?: return@forEach
            menuGroup.handle(
                ReOrderMenuGroupCommand(
                    priority = it.priority,
                    updatedAt = now,
                )
            )
        }
        repository.bulkUpdatePriority(menuGroups)
    }

    suspend fun deleteMenuGroup(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 메뉴그룹을 찾을 수 없습니다.")
        repository.delete(id)
    }

    suspend fun existsById(id: Long): Boolean = repository.existsById(id)

    private suspend fun findMenuGroupByIdOrThrow(id: Long): MenuGroup = repository.findById(id)
        ?: throw NoSuchElementException("${id}번 메뉴그룹을 찾을 수 없습니다.")

}