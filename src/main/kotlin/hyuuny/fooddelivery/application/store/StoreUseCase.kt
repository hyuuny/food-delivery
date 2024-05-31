package hyuuny.fooddelivery.application.store

import CreateStoreCommand
import CreateStoreRequest
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.infrastructure.store.StoreRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StoreUseCase(
    private val repository: StoreRepository,
) {

    suspend fun createStore(request: CreateStoreRequest, now: LocalDateTime): Store {
        val store = Store.handle(
            CreateStoreCommand(
                categoryId = request.categoryId,
                deliveryType = request.deliveryType,
                name = request.name,
                ownerName = request.ownerName,
                taxId = request.taxId,
                deliveryFee = request.deliveryFee,
                minimumOrderAmount = request.minimumOrderAmount,
                iconImageUrl = request.iconImageUrl,
                description = request.description,
                foodOrigin = request.foodOrigin,
                phoneNumber = request.phoneNumber,
                createdAt = now,
                updatedAt = now
            )
        )
        return repository.insert(store)
    }

}