package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.StoreDetail
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreDetailDao : CoroutineCrudRepository<StoreDetail, Long> {
}