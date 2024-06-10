package hyuuny.fooddelivery.application.store

import hyuuny.fooddelivery.domain.store.StoreDetail
import hyuuny.fooddelivery.infrastructure.store.StoreDetailRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class StoreDetailUseCase(
    private val repository: StoreDetailRepository
) {

    suspend fun getStoreDetailByStoreId(storeId: Long): StoreDetail = findStoreDetailByStoreIdOrThrow(storeId)

    @Transactional
    suspend fun deleteStoreDetailByStoreId(storeId: Long) = repository.deleteByStoreId(storeId)

    private suspend fun findStoreDetailByStoreIdOrThrow(storeId: Long) = repository.findByStoreId(storeId)
        ?: throw NoSuchElementException("존재하지 않는 매장 정보입니다.")

}
