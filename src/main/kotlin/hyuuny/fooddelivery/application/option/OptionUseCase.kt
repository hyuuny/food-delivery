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
import java.time.LocalDateTime

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

    suspend fun createOption(request: CreateOptionRequest): Option {
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

    suspend fun updateOption(id: Long, request: UpdateOptionRequest) {
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

    suspend fun deleteOption(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("옵션을 찾을 수 없습니다.")
        repository.delete(id)
    }

    suspend fun getAllByOptionGroupIds(optionGroupIds: List<Long>): List<Option> =
        repository.findAllByOptionGroupIdIn(optionGroupIds)

    private suspend fun findOptionByIdOrThrow(id: Long): Option = repository.findById(id)
        ?: throw NoSuchElementException("옵션을 찾을 수 없습니다.")

}