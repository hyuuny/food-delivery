package hyuuny.fooddelivery.application.menugroup

import CreateMenuGroupCommand
import CreateMenuGroupRequest
import MenuGroupSearchCondition
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
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(menuGroup)
    }

    suspend fun getMenuGroup(id: Long): MenuGroup {
        return repository.findById(id)
            ?: throw IllegalStateException("${id}번 메뉴그룹을 찾을 수 없습니다.")
    }

}