package hyuuny.fooddelivery.application.store

import CreateStoreCommand
import CreateStoreRequest
import StoreSearchCondition
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.infrastructure.store.StoreRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StoreUseCase(
    private val repository: StoreRepository,
) {

    suspend fun getStores(searchCondition: StoreSearchCondition, pageable: Pageable): Page<Store> {
        val page = repository.findAllStores(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

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

    suspend fun getStore(id: Long): Store = repository.findById(id)
        ?: throw NoSuchElementException("존재하지 않는 매장입니다.")

}