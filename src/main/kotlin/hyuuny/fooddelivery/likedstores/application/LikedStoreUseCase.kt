package hyuuny.fooddelivery.likedstores.application

import LikeOrCancelCommand
import LikeOrCancelRequest
import hyuuny.fooddelivery.likedstores.domain.LikedStore
import hyuuny.fooddelivery.likedstores.infrastructure.LikedStoreRepository
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.users.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class LikedStoreUseCase(
    private val repository: LikedStoreRepository,
) {

    @Transactional
    suspend fun likeOrCancel(
        request: LikeOrCancelRequest,
        getUser: suspend () -> User,
        getStore: suspend () -> Store,
    ) {
        val now = LocalDateTime.now()
        val user = getUser()
        val store = getStore()

        val likedStore = repository.findByUserIdAndStoreId(user.id!!, store.id!!)
        likedStore?.let { repository.delete(it.id!!) } ?: repository.insert(
            LikedStore.handle(
                LikeOrCancelCommand(
                    userId = user.id!!,
                    storeId = store.id!!,
                    createdAt = now,
                )
            )
        )
    }

    suspend fun getAllByUserId(userId: Long): List<LikedStore> = repository.findAllByUserId(userId)

}
