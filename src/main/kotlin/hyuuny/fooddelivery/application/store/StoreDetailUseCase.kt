package hyuuny.fooddelivery.application.store

import CreateStoreDetailCommand
import CreateStoreDetailRequest
import hyuuny.fooddelivery.domain.store.StoreDetail
import hyuuny.fooddelivery.infrastructure.store.StoreDetailRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StoreDetailUseCase(
    private val repository: StoreDetailRepository
) {

    suspend fun createStoreDetail(storeId: Long, request: CreateStoreDetailRequest, now: LocalDateTime): StoreDetail {
        val storeDetail = StoreDetail.handle(
            CreateStoreDetailCommand(
                storeId = storeId,
                zipCode = request.zipCode,
                address = request.address,
                detailedAddress = request.detailedAddress,
                openHours = request.openHours,
                closedDay = request.closedDay,
                createdAt = now,
            )
        )
        return repository.insert(storeDetail)
    }

    suspend fun getStoreDetailByStoreId(storeId: Long): StoreDetail = repository.findByStoreId(storeId)
        ?: throw NoSuchElementException("존재하지 않는 매장 정보입니다.")

}