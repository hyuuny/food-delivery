package hyuuny.fooddelivery.options.infrastructure

import hyuuny.fooddelivery.options.domain.Option
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OptionDao : CoroutineCrudRepository<Option, Long> {

    fun findAllByOptionGroupIdIn(optionGroupIds: List<Long>): Flow<Option>

}
