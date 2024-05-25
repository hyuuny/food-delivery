package hyuuny.fooddelivery.infrastructure.optiongroup

import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OptionGroupDao : CoroutineCrudRepository<OptionGroup, Long> {

    suspend fun findAllByMenuId(menuId: Long): List<OptionGroup>

}