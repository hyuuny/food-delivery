package hyuuny.fooddelivery.infrastructure.optiongroup

import OptionGroupSearchCondition
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OptionGroupRepository {

    suspend fun insert(optionGroup: OptionGroup): OptionGroup

    suspend fun findById(id: Long): OptionGroup?

    suspend fun update(optionGroup: OptionGroup)

    suspend fun delete(id: Long)

    suspend fun findAllOptionGroups(searchCondition: OptionGroupSearchCondition, pageable: Pageable): Page<OptionGroup>

    suspend fun findAllByMenuId(menuId: Long): List<OptionGroup>

    suspend fun bulkUpdatePriority(optionGroups: List<OptionGroup>)

    suspend fun existsById(id: Long): Boolean

}