package hyuuny.fooddelivery.application.likedstore

import hyuuny.fooddelivery.domain.likedstore.LikedStore
import hyuuny.fooddelivery.infrastructure.likedstore.LikedStoreRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetAllLikedStoreUseCaseTest : BehaviorSpec({

    val repository = mockk<LikedStoreRepository>()
    val useCase = LikedStoreUseCase(repository)

    given("회원이 찜 목록을 조회할 때") {
        val userId = 1L
        val storeIds = listOf(77L, 88L, 99L, 111L, 222L)

        val now = LocalDateTime.now()
        val likedStores = storeIds.mapIndexed { idx, storeId ->
            LikedStore(
                id = idx.toLong() + 1,
                userId = userId,
                storeId = storeId,
                createdAt = now
            )
        }
        coEvery { repository.findAllByUserId(any()) } returns likedStores

        `when`("찜 내역이 존재하면") {
            val result = useCase.getAllByUserId(userId)

            then("찜 목록을 조회할 수 있다.") {
                result.size shouldBe likedStores.size
                result.forEachIndexed { idx, liked ->
                    liked.userId shouldBe likedStores[idx].userId
                    liked.storeId shouldBe likedStores[idx].storeId
                }
            }
        }

        `when`("찜 내역이 존재하지 않으면") {
            coEvery { repository.findAllByUserId(any()) } returns emptyList()
            val result = useCase.getAllByUserId(2L)

            then("빈 목록이 조회된다.") {
                result.size shouldBe 0
            }
        }
    }
})
