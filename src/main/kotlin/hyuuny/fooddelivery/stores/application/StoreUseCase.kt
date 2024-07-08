package hyuuny.fooddelivery.stores.application

import AdminStoreSearchCondition
import ApiStoreSearchCondition
import CreateStoreCommand
import CreateStoreDetailCommand
import CreateStoreImageCommand
import CreateStoreRequest
import UpdateStoreCommand
import UpdateStoreRequest
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.domain.StoreDetail
import hyuuny.fooddelivery.stores.domain.StoreImage
import hyuuny.fooddelivery.stores.infrastructure.StoreDetailRepository
import hyuuny.fooddelivery.stores.infrastructure.StoreImageRepository
import hyuuny.fooddelivery.stores.infrastructure.StoreRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class StoreUseCase(
    private val repository: StoreRepository,
    private val storeDetailRepository: StoreDetailRepository,
    private val storeImageRepository: StoreImageRepository,
) {

    suspend fun getStoresByAdminCondition(searchCondition: AdminStoreSearchCondition, pageable: Pageable): Page<Store> {
        val page = repository.findAllStores(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    suspend fun getStoresByApiCondition(searchCondition: ApiStoreSearchCondition, pageable: Pageable): Page<Store> {
        val page = repository.findAllStores(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    @Transactional
    suspend fun createStore(request: CreateStoreRequest): Store {
        val now = LocalDateTime.now()
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
        val savedStore = repository.insert(store)

        StoreDetail.handle(
            CreateStoreDetailCommand(
                storeId = savedStore.id!!,
                zipCode = request.storeDetail.zipCode,
                address = request.storeDetail.address,
                detailedAddress = request.storeDetail.detailedAddress,
                openHours = request.storeDetail.openHours,
                closedDay = request.storeDetail.closedDay,
                createdAt = now,
            )
        ).apply {
            storeDetailRepository.insert(this)
        }

        val storeImages = request.storeImage?.imageUrls?.map {
            StoreImage.handle(
                CreateStoreImageCommand(
                    storeId = savedStore.id!!,
                    imageUrl = it,
                    createdAt = now
                )
            )
        }
        storeImages?.takeIf { it.isNotEmpty() }?.also { storeImageRepository.insertAll(it) }
        return savedStore
    }

    suspend fun getStore(id: Long): Store = findStoreByIdOrThrow(id)

    @Transactional
    suspend fun updateStore(id: Long, request: UpdateStoreRequest): Store {
        val now = LocalDateTime.now()
        val store = findStoreByIdOrThrow(id)
        store.handle(
            UpdateStoreCommand(
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
                updatedAt = now
            )
        )
        repository.update(store)

        storeDetailRepository.deleteByStoreId(id)
        val storeDetail = StoreDetail.handle(
            CreateStoreDetailCommand(
                storeId = store.id!!,
                zipCode = request.storeDetail.zipCode,
                address = request.storeDetail.address,
                detailedAddress = request.storeDetail.detailedAddress,
                openHours = request.storeDetail.openHours,
                closedDay = request.storeDetail.closedDay,
                createdAt = now,
            )
        )
        storeDetailRepository.insert(storeDetail)

        storeImageRepository.deleteAllByStoreId(id)
        val storeImages = request.storeImage?.imageUrls?.map {
            StoreImage.handle(
                CreateStoreImageCommand(
                    storeId = store.id!!,
                    imageUrl = it,
                    createdAt = now
                )
            )
        }
        storeImages?.takeIf { it.isNotEmpty() }?.also {
            storeImageRepository.insertAll(it)
        }
        return store
    }


    @Transactional
    suspend fun deleteStore(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 매장을 찾을 수 없습니다.")
        storeImageRepository.deleteAllByStoreId(id)
        storeDetailRepository.deleteByStoreId(id)
        repository.delete(id)
    }

    suspend fun getAllByIds(ids: List<Long>): List<Store> = repository.findAllByIdIn(ids)

    private suspend fun findStoreByIdOrThrow(id: Long) = repository.findById(id)
        ?: throw NoSuchElementException("${id}번 매장을 찾을 수 없습니다.")
}
