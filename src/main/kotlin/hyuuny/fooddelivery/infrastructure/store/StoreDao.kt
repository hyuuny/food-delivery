package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.Store
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreDao : CoroutineCrudRepository<Store, Long> {
}