package hyuuny.fooddelivery.optiongroups.infrastructure

import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OptionGroupDao : CoroutineCrudRepository<OptionGroup, Long> {

    suspend fun findAllByMenuId(menuId: Long): List<OptionGroup>

}
