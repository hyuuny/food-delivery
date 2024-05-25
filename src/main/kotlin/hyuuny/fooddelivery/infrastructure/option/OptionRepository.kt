package hyuuny.fooddelivery.infrastructure.option

import OptionSearchCondition
import hyuuny.fooddelivery.domain.option.Option
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OptionRepository {

    suspend fun insert(option: Option): Option

    suspend fun findById(id: Long): Option?

    suspend fun update(option: Option)

    suspend fun delete(id: Long)

    suspend fun findAllOptions(searchCondition: OptionSearchCondition, pageable: Pageable): Page<Option>

    suspend fun existsById(id: Long): Boolean

}