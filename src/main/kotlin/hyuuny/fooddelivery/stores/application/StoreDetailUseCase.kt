package hyuuny.fooddelivery.stores.application

import hyuuny.fooddelivery.stores.domain.StoreDetail
import hyuuny.fooddelivery.stores.infrastructure.StoreDetailRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class StoreDetailUseCase(
    private val repository: StoreDetailRepository
) {

    suspend fun getStoreDetailByStoreId(storeId: Long): StoreDetail = findStoreDetailByStoreIdOrThrow(storeId)

    private suspend fun findStoreDetailByStoreIdOrThrow(storeId: Long) = repository.findByStoreId(storeId)
        ?: throw NoSuchElementException("${storeId}번 매장 정보를 찾을 수 없습니다.")

}