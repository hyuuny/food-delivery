package hyuuny.fooddelivery.likedstores.presentation.api.v1

import LikeOrCancelRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.likedstores.application.LikedStoreUseCase
import hyuuny.fooddelivery.likedstores.domain.LikedStore
import hyuuny.fooddelivery.reviews.application.ReviewUseCase
import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.stores.domain.Store
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

    @MockkBean
    private lateinit var reviewUseCase: ReviewUseCase

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
                minimumOrderAmount = (idx + 1) * 1000L,
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

        val randomScores = storeIds.map { (Math.random() * 5 + 1).toInt() }
        val reviews = storeIds.mapIndexed { idx, it ->
            Review(
                id = it,
                userId = userId,
                storeId = storeIds[idx],
                orderId = idx.toLong() + 1,
                score = randomScores[idx],
                content = "맛있어요. 다음에 또 주문할게요.",
                createdAt = now,
            )
        }
        val averageScoreMap = reviews.groupBy { it.storeId }
            .mapValues { (_, reviews) -> reviews.map { it.score }.average() }

        coEvery { useCase.getAllByUserId(any()) } returns likedStores
        coEvery { storeUseCase.getAllByIds(any()) } returns stores
        coEvery { reviewUseCase.getAverageScoreByStoreIds(any()) } returns averageScoreMap

        webTestClient.get().uri("/api/v1/users/$userId/liked-stores")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.[0].id").isEqualTo(likedStores[4].id!!)
            .jsonPath("$.[0].userId").isEqualTo(likedStores[4].userId)
            .jsonPath("$.[0].storeId").isEqualTo(likedStores[4].storeId)
            .jsonPath("$.[0].deliveryType").isEqualTo(stores[4].deliveryType.name)
            .jsonPath("$.[0].storeName").isEqualTo(stores[4].name)
            .jsonPath("$.[0].storeDescription").isEqualTo(stores[4].description)
            .jsonPath("$.[0].storeMinimumOrderAmount").isEqualTo(stores[4].minimumOrderAmount)
            .jsonPath("$.[0].averageScore").isEqualTo(averageScoreMap[storeIds[4]]!!)
            .jsonPath("$.[0].deliveryFee").isEqualTo(stores[4].deliveryFee)

            .jsonPath("$.[1].id").isEqualTo(likedStores[3].id!!)
            .jsonPath("$.[1].userId").isEqualTo(likedStores[3].userId)
            .jsonPath("$.[1].storeId").isEqualTo(likedStores[3].storeId)
            .jsonPath("$.[1].deliveryType").isEqualTo(stores[3].deliveryType.name)
            .jsonPath("$.[1].storeName").isEqualTo(stores[3].name)
            .jsonPath("$.[1].storeDescription").isEqualTo(stores[3].description)
            .jsonPath("$.[1].storeMinimumOrderAmount").isEqualTo(stores[3].minimumOrderAmount)
            .jsonPath("$.[1].averageScore").isEqualTo(averageScoreMap[storeIds[3]]!!)
            .jsonPath("$.[1].deliveryFee").isEqualTo(stores[3].deliveryFee)

            .jsonPath("$.[2].id").isEqualTo(likedStores[2].id!!)
            .jsonPath("$.[2].userId").isEqualTo(likedStores[2].userId)
            .jsonPath("$.[2].storeId").isEqualTo(likedStores[2].storeId)
            .jsonPath("$.[2].deliveryType").isEqualTo(stores[2].deliveryType.name)
            .jsonPath("$.[2].storeName").isEqualTo(stores[2].name)
            .jsonPath("$.[2].storeDescription").isEqualTo(stores[2].description)
            .jsonPath("$.[2].storeMinimumOrderAmount").isEqualTo(stores[2].minimumOrderAmount)
            .jsonPath("$.[2].averageScore").isEqualTo(averageScoreMap[storeIds[2]]!!)
            .jsonPath("$.[2].deliveryFee").isEqualTo(stores[2].deliveryFee)

            .jsonPath("$.[3].id").isEqualTo(likedStores[1].id!!)
            .jsonPath("$.[3].userId").isEqualTo(likedStores[1].userId)
            .jsonPath("$.[3].storeId").isEqualTo(likedStores[1].storeId)
            .jsonPath("$.[3].deliveryType").isEqualTo(stores[1].deliveryType.name)
            .jsonPath("$.[3].storeName").isEqualTo(stores[1].name)
            .jsonPath("$.[3].storeDescription").isEqualTo(stores[1].description)
            .jsonPath("$.[3].storeMinimumOrderAmount").isEqualTo(stores[1].minimumOrderAmount)
            .jsonPath("$.[3].averageScore").isEqualTo(averageScoreMap[storeIds[1]]!!)
            .jsonPath("$.[3].deliveryFee").isEqualTo(stores[1].deliveryFee)

            .jsonPath("$.[4].id").isEqualTo(likedStores[0].id!!)
            .jsonPath("$.[4].userId").isEqualTo(likedStores[0].userId)
            .jsonPath("$.[4].storeId").isEqualTo(likedStores[0].storeId)
            .jsonPath("$.[4].deliveryType").isEqualTo(stores[0].deliveryType.name)
            .jsonPath("$.[4].storeName").isEqualTo(stores[0].name)
            .jsonPath("$.[4].storeDescription").isEqualTo(stores[0].description)
            .jsonPath("$.[4].storeMinimumOrderAmount").isEqualTo(stores[0].minimumOrderAmount)
            .jsonPath("$.[4].averageScore").isEqualTo(averageScoreMap[storeIds[0]]!!)
            .jsonPath("$.[4].deliveryFee").isEqualTo(stores[0].deliveryFee)
    }

}
