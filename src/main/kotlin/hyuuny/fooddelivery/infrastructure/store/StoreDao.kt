package hyuuny.fooddelivery.infrastructure.store

import hyuuny.fooddelivery.domain.store.Store
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreDao : CoroutineCrudRepository<Store, Long> {

    suspend fun findAllByNameContaining(name: String): List<Store>

    suspend fun findAllByIdIn(ids: List<Long>): List<Store>

}