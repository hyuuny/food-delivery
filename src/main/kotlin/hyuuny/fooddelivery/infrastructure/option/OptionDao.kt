package hyuuny.fooddelivery.infrastructure.option

import hyuuny.fooddelivery.domain.option.Option
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OptionDao : CoroutineCrudRepository<Option, Long> {

    fun findAllByOptionGroupIdIn(optionGroupIds: List<Long>): Flow<Option>

}