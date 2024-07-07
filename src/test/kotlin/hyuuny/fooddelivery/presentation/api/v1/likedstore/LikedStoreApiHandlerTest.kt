package hyuuny.fooddelivery.presentation.api.v1.likedstore

import LikeOrCancelRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.likedstore.LikedStoreUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.likedstore.LikedStore
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class LikedStoreApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: LikedStoreUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

    @DisplayName("회원은 찜하지 않은 매장이면 찜하거나, 찜한 매장이면 찜을 취소할 수 있다.")
    @Test
    fun toLike() {
        val userId = 1L
        val storeId = 77L

        val request = LikeOrCancelRequest(
            userId = userId,
            storeId = storeId
        )
        coEvery { useCase.likeOrCancel(any(), any(), any()) } returns Unit

        webTestClient.post().uri("/api/v1/liked-stores")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("회원은 자신이 찜한 매장 목록을 조회할 수 있다.")
    @Test
    fun getAllLikedStores() {
        val userId = 1L
        val storeIds = listOf(77L, 88L, 99L, 111L, 222L)

        val now = LocalDateTime.now()
        val stores = storeIds.mapIndexed { idx, it ->
            Store(
                id = it,
                categoryId = 2L,
                deliveryType = DeliveryType.OUTSOURCING,
                name = "${it}번가 피자",
                ownerName = "나피자",
                taxId = "125-21-3892${idx}",
                deliveryFee = 0,
                minimumOrderAmount = (idx + 1) * 10000L,
                iconImageUrl = "icon-image-url-1.jpg",
                description = "안녕하세요!",
                foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
                phoneNumber = "02-1231-2308",
                createdAt = now,
                updatedAt = now,
            )
        }

        val likedStores = storeIds.mapIndexed { idx, storeId ->
            LikedStore(
                id = idx.toLong() + 1,
                userId = userId,
                storeId = storeId,
                createdAt = now
            )
        }

        coEvery { useCase.getAllByUserId(any()) } returns likedStores
        coEvery { storeUseCase.getAllByIds(any()) } returns stores

        webTestClient.get().uri("/api/v1/users/$userId/liked-stores")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.[0].id").isEqualTo(likedStores[4].id!!)
            .jsonPath("$.[0].userId").isEqualTo(likedStores[4].userId)
            .jsonPath("$.[0].storeId").isEqualTo(likedStores[4].storeId)
            .jsonPath("$.[0].storeName").isEqualTo(stores[4].name)
            .jsonPath("$.[0].storeDescription").isEqualTo(stores[4].description)
            .jsonPath("$.[0].storeMinimumOrderAmount").isEqualTo(stores[4].minimumOrderAmount)

            .jsonPath("$.[1].id").isEqualTo(likedStores[3].id!!)
            .jsonPath("$.[1].userId").isEqualTo(likedStores[3].userId)
            .jsonPath("$.[1].storeId").isEqualTo(likedStores[3].storeId)
            .jsonPath("$.[1].storeName").isEqualTo(stores[3].name)
            .jsonPath("$.[1].storeDescription").isEqualTo(stores[3].description)
            .jsonPath("$.[1].storeMinimumOrderAmount").isEqualTo(stores[3].minimumOrderAmount)

            .jsonPath("$.[2].id").isEqualTo(likedStores[2].id!!)
            .jsonPath("$.[2].userId").isEqualTo(likedStores[2].userId)
            .jsonPath("$.[2].storeId").isEqualTo(likedStores[2].storeId)
            .jsonPath("$.[2].storeName").isEqualTo(stores[2].name)
            .jsonPath("$.[2].storeDescription").isEqualTo(stores[2].description)
            .jsonPath("$.[2].storeMinimumOrderAmount").isEqualTo(stores[2].minimumOrderAmount)

            .jsonPath("$.[3].id").isEqualTo(likedStores[1].id!!)
            .jsonPath("$.[3].userId").isEqualTo(likedStores[1].userId)
            .jsonPath("$.[3].storeId").isEqualTo(likedStores[1].storeId)
            .jsonPath("$.[3].storeName").isEqualTo(stores[1].name)
            .jsonPath("$.[3].storeDescription").isEqualTo(stores[1].description)
            .jsonPath("$.[3].storeMinimumOrderAmount").isEqualTo(stores[1].minimumOrderAmount)

            .jsonPath("$.[4].id").isEqualTo(likedStores[0].id!!)
            .jsonPath("$.[4].userId").isEqualTo(likedStores[0].userId)
            .jsonPath("$.[4].storeId").isEqualTo(likedStores[0].storeId)
            .jsonPath("$.[4].storeName").isEqualTo(stores[0].name)
            .jsonPath("$.[4].storeDescription").isEqualTo(stores[0].description)
            .jsonPath("$.[4].storeMinimumOrderAmount").isEqualTo(stores[0].minimumOrderAmount)
    }

}
