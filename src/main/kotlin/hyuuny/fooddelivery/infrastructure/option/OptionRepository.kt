package hyuuny.fooddelivery.infrastructure.option

import AdminOptionSearchCondition
import hyuuny.fooddelivery.domain.option.Option
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OptionRepository {

    suspend fun insert(option: Option): Option

    suspend fun findById(id: Long): Option?

    suspend fun update(option: Option)

    suspend fun delete(id: Long)

    suspend fun findAllOptions(searchCondition: AdminOptionSearchCondition, pageable: Pageable): Page<Option>

    suspend fun existsById(id: Long): Boolean

    suspend fun findAllByOptionGroupIdIn(optionGroupIds: List<Long>): List<Option>

}