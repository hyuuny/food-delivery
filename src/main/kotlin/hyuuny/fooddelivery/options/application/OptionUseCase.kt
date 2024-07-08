package hyuuny.fooddelivery.options.application

import AdminOptionSearchCondition
import ChangeOptionGroupIdCommand
import CreateOptionCommand
import CreateOptionRequest
import UpdateOptionCommand
import UpdateOptionRequest
import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import hyuuny.fooddelivery.options.domain.Option
import hyuuny.fooddelivery.options.infrastructure.OptionRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class OptionUseCase(
    private val repository: OptionRepository
) {

    suspend fun getOptionsByAdminCondition(
        searchCondition: AdminOptionSearchCondition,
        pageable: Pageable
    ): PageImpl<Option> {
        val page = repository.findAllOptions(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    @Transactional
    suspend fun createOption(
        request: CreateOptionRequest,
        getOptionGroup: suspend () -> OptionGroup,
    ): Option {
        if (request.name.isBlank()) throw IllegalArgumentException("옵션명은 공백일수 없습니다.")

        val now = LocalDateTime.now()
        val optionGroup = getOptionGroup()
        val option = Option.handle(
            CreateOptionCommand(
                optionGroupId = optionGroup.id!!,
                name = request.name,
                price = request.price,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(option)
    }

    suspend fun getOption(id: Long): Option {
        return findOptionByIdOrThrow(id)
    }

    @Transactional
    suspend fun updateOption(id: Long, request: UpdateOptionRequest) {
        if (request.name.isBlank()) throw IllegalArgumentException("옵션명은 공백일수 없습니다.")

        val now = LocalDateTime.now()
        val option = findOptionByIdOrThrow(id)
        option.handle(
            UpdateOptionCommand(
                name = request.name,
                price = request.price,
                updatedAt = now
            )
        )
        repository.update(option)
    }

    @Transactional
    suspend fun changeOptionGroup(
        id: Long,
        getOptionGroup: suspend () -> OptionGroup
    ) {
        val now = LocalDateTime.now()
        val optionGroup = getOptionGroup()
        val option = findOptionByIdOrThrow(id)
        option.handle(
            ChangeOptionGroupIdCommand(
                optionGroupId = optionGroup.id!!,
                updatedAt = now,
            )
        )
        repository.updateOptionGroupId(option)
    }

    @Transactional
    suspend fun deleteOption(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 옵션을 찾을 수 없습니다.")
        repository.delete(id)
    }

    suspend fun getAllByOptionGroupIds(optionGroupIds: List<Long>): List<Option> =
        repository.findAllByOptionGroupIdIn(optionGroupIds)

    suspend fun getAllByIds(ids: List<Long>): List<Option> = repository.findAllByIdIn(ids)

    private suspend fun findOptionByIdOrThrow(id: Long): Option = repository.findById(id)
        ?: throw NoSuchElementException("${id}번 옵션을 찾을 수 없습니다.")

}
