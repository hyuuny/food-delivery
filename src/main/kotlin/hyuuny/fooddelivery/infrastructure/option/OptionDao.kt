package hyuuny.fooddelivery.infrastructure.option

import hyuuny.fooddelivery.domain.option.Option
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OptionDao : CoroutineCrudRepository<Option, Long> {
}