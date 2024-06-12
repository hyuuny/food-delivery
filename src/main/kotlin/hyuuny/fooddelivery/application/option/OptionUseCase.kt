package hyuuny.fooddelivery.application.option

import AdminOptionSearchCondition
import CreateOptionCommand
import CreateOptionRequest
import UpdateOptionCommand
import UpdateOptionRequest
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.infrastructure.option.OptionRepository
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
    suspend fun createOption(request: CreateOptionRequest): Option {
        if (request.name.isBlank()) throw IllegalArgumentException("옵션명은 공백일수 없습니다.")

        val now = LocalDateTime.now()
        val option = Option.handle(
            CreateOptionCommand(
                optionGroupId = request.optionGroupId,
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