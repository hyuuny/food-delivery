package hyuuny.fooddelivery.application.optiongroup

import AdminOptionGroupSearchCondition
import CreateOptionGroupCommand
import CreateOptionGroupRequest
import ReOrderOptionGroupCommand
import ReorderOptionGroupRequests
import UpdateOptionGroupCommand
import UpdateOptionGroupRequest
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import hyuuny.fooddelivery.infrastructure.optiongroup.OptionGroupRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class OptionGroupUseCase(
    private val repository: OptionGroupRepository
) {

    suspend fun getOptionGroupsByAdminCondition(
        searchCondition: AdminOptionGroupSearchCondition,
        pageable: Pageable
    ): PageImpl<OptionGroup> {
        val page = repository.findAllOptionGroups(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    @Transactional
    suspend fun createOptionGroup(
        request: CreateOptionGroupRequest,
        getMenu: suspend () -> Menu,
    ): OptionGroup {
        if (request.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

        val now = LocalDateTime.now()
        val menu = getMenu()
        val optionGroup = OptionGroup.handle(
            CreateOptionGroupCommand(
                menuId = menu.id!!,
                name = request.name,
                required = request.required,
                priority = request.priority,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(optionGroup)
    }

    suspend fun getOptionGroup(id: Long): OptionGroup = findOptionGroupByIdOrThrow(id)

    @Transactional
    suspend fun updateOptionGroup(id: Long, request: UpdateOptionGroupRequest) {
        if (request.name.length < 2) throw IllegalArgumentException("이름은 2자 이상이어야 합니다.")

        val now = LocalDateTime.now()
        val optionGroup = findOptionGroupByIdOrThrow(id)
        optionGroup.handle(
            UpdateOptionGroupCommand(
                name = request.name,
                required = request.required,
                updatedAt = now,
            )
        )
        repository.update(optionGroup)
    }

    @Transactional
    suspend fun reOrderOptionGroups(
        request: ReorderOptionGroupRequests,
        getMenu: suspend () -> Menu,
    ) {
        val now = LocalDateTime.now()
        val menu = getMenu()
        val optionGroups = repository.findAllByMenuId(menu.id!!)

        if (optionGroups.size != request.reOrderedOptionGroups.size) throw IllegalStateException("옵션그룹의 개수가 일치하지 않습니다.")

        val optionGroupMap = optionGroups.associateBy { it.id }
        request.reOrderedOptionGroups.forEach {
            val optionGroup = optionGroupMap[it.optionGroupId] ?: return@forEach
            optionGroup.handle(
                ReOrderOptionGroupCommand(
                    priority = it.priority,
                    updatedAt = now,
                )
            )
        }
        repository.bulkUpdatePriority(optionGroups)
    }

    @Transactional
    suspend fun deleteOptionGroup(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 옵션그룹을 찾을 수 없습니다.")
        repository.delete(id)
    }

    suspend fun existsById(id: Long): Boolean = repository.existsById(id)

    suspend fun getAllByMenuId(menuId: Long): List<OptionGroup> = repository.findAllByMenuId(menuId)

    private suspend fun findOptionGroupByIdOrThrow(id: Long): OptionGroup = repository.findById(id)
        ?: throw NoSuchElementException("${id}번 옵션그룹을 찾을 수 없습니다.")

}