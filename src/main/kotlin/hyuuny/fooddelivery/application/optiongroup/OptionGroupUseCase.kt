package hyuuny.fooddelivery.application.optiongroup

import AdminOptionGroupSearchCondition
import CreateOptionGroupCommand
import CreateOptionGroupRequest
import ReOrderOptionGroupCommand
import ReorderOptionGroupRequests
import UpdateOptionGroupCommand
import UpdateOptionGroupRequest
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import hyuuny.fooddelivery.infrastructure.optiongroup.OptionGroupRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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

    suspend fun createOptionGroup(request: CreateOptionGroupRequest): OptionGroup {
        val now = LocalDateTime.now()
        val optionGroup = OptionGroup.handle(
            CreateOptionGroupCommand(
                menuId = request.menuId,
                name = request.name,
                required = request.required,
                priority = request.priority,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(optionGroup)
    }

    suspend fun getOptionGroup(id: Long): OptionGroup {
        return findOptionGroupByIdOrThrow(id)
    }

    suspend fun updateOptionGroup(id: Long, request: UpdateOptionGroupRequest) {
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

    suspend fun reOrderOptionGroups(menuId: Long, request: ReorderOptionGroupRequests) {
        val now = LocalDateTime.now()
        val optionGroups = repository.findAllByMenuId(menuId)

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

    suspend fun deleteOptionGroup(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("존재하지 않는 옵션그룹입니다.")
        repository.delete(id)
    }

    suspend fun existsById(id: Long): Boolean = repository.existsById(id)

    suspend fun getAllByMenuId(menuId: Long): List<OptionGroup> = repository.findAllByMenuId(menuId)

    private suspend fun findOptionGroupByIdOrThrow(id: Long): OptionGroup = repository.findById(id)
        ?: throw NoSuchElementException("존재하지 않는 옵션그룹입니다.")

}