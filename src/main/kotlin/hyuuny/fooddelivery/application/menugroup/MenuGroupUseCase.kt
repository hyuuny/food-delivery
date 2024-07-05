package hyuuny.fooddelivery.application.menugroup

import AdminMenuGroupSearchCondition
import CreateMenuGroupCommand
import CreateMenuGroupRequest
import ReOrderMenuGroupCommand
import ReorderMenuGroupRequests
import UpdateMenuGroupCommand
import UpdateMenuGroupRequest
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class MenuGroupUseCase(
    private val repository: MenuGroupRepository
) {

    suspend fun getMenuGroupsByAdminCondition(
        searchCondition: AdminMenuGroupSearchCondition,
        pageable: Pageable
    ): PageImpl<MenuGroup> {
        val page = repository.findAllMenuGroups(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    @Transactional
    suspend fun createMenuGroup(
        request: CreateMenuGroupRequest,
        getStore: suspend () -> Store,
    ): MenuGroup {
        if (request.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

        val now = LocalDateTime.now()
        val store = getStore()
        val menuGroup = MenuGroup.handle(
            CreateMenuGroupCommand(
                storeId = store.id!!,
                name = request.name,
                priority = request.priority,
                description = request.description,
                createdAt = now,
                updatedAt = now,
            )
        )
        return repository.insert(menuGroup)
    }

    suspend fun getMenuGroup(id: Long): MenuGroup = findMenuGroupByIdOrThrow(id)

    @Transactional
    suspend fun updateMenuGroup(id: Long, request: UpdateMenuGroupRequest) {
        if (request.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

        val now = LocalDateTime.now()
        val menuGroup = findMenuGroupByIdOrThrow(id)
        menuGroup.handle(
            UpdateMenuGroupCommand(
                name = request.name,
                description = request.description,
                updatedAt = now
            )
        )
        repository.update(menuGroup)
    }

    @Transactional
    suspend fun reOrderMenuGroups(
        requests: ReorderMenuGroupRequests,
        getStore: suspend () -> Store
    ) {
        val now = LocalDateTime.now()
        val store = getStore()
        val menuGroups = repository.findAllByStoreId(store.id!!)

        if (menuGroups.size != requests.reOrderedMenuGroups.size) throw IllegalStateException("메뉴그룹의 개수가 일치하지 않습니다.")

        val menuGroupMap = menuGroups.associateBy { it.id }
        requests.reOrderedMenuGroups.forEach {
            val menuGroup = menuGroupMap[it.menuGroupId] ?: return@forEach
            menuGroup.handle(
                ReOrderMenuGroupCommand(
                    priority = it.priority,
                    updatedAt = now
                )
            )
        }
        repository.bulkUpdatePriority(menuGroups)
    }

    suspend fun getAllByStoreId(storeId: Long) = repository.findAllByStoreId(storeId)

    suspend fun getAllByStoreIds(storeIds: List<Long>) = repository.findAllByStoreIdIn(storeIds)

    @Transactional
    suspend fun deleteMenuGroup(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 메뉴그룹을 찾을 수 없습니다.")
        repository.delete(id)
    }

    private suspend fun findMenuGroupByIdOrThrow(id: Long): MenuGroup =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 메뉴그룹을 찾을 수 없습니다.")

}
