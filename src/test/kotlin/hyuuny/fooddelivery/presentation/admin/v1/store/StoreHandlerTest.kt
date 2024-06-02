package hyuuny.fooddelivery.presentation.admin.v1.store

import CreateStoreDetailRequest
import CreateStoreImageRequest
import CreateStoreRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.store.StoreDetailUseCase
import hyuuny.fooddelivery.application.store.StoreImageUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.domain.store.DeliveryType
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.store.StoreDetail
import hyuuny.fooddelivery.domain.store.StoreImage
import hyuuny.fooddelivery.infrastructure.store.StoreDetailRepository
import hyuuny.fooddelivery.infrastructure.store.StoreImageRepository
import hyuuny.fooddelivery.infrastructure.store.StoreRepository
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class StoreHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: StoreUseCase

    @MockkBean
    private lateinit var storeDetailUseCase: StoreDetailUseCase

    @MockkBean
    private lateinit var storeImageUseCase: StoreImageUseCase

    @MockkBean
    private lateinit var repository: StoreRepository

    @MockkBean
    private lateinit var storeDetailRepository: StoreDetailRepository

    @MockkBean
    private lateinit var storeImageRepository: StoreImageRepository

    @DisplayName("매장을 등록할 수 있다.")
    @Test
    fun createStore() {
        val request = CreateStoreRequest(
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며,\n BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            storeDetail = CreateStoreDetailRequest(
                zipCode = "12345",
                address = "서울시 강남구 강남대로123길 12",
                detailedAddress = "1층 101호",
                openHours = "매일 오전 11:00 ~ 오후 11시 30분",
                closedDay = null,
            ),
            storeImage = CreateStoreImageRequest(
                imageUrls = listOf(
                    "image-url-1.jpg",
                    "image-url-2.jpg",
                    "image-url-3.jpg",
                )
            )
        )
        val now = LocalDateTime.now()
        val store = generateStore(request, now)
        val storeDetail = generateStoreDetail(store.id!!, request.storeDetail, now)
        val storeImages = generateStoreImage(store.id!!, request.storeImage, now)
        coEvery { useCase.createStore(any(), any()) } returns store
        coEvery { storeDetailUseCase.createStoreDetail(any(), any(), any()) } returns storeDetail
        coEvery { storeImageUseCase.createStoreImages(any(), any(), any()) } returns storeImages

        webTestClient.post().uri("/admin/v1/stores")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(store.id!!)
            .jsonPath("$.categoryId").isEqualTo(store.categoryId)
            .jsonPath("$.deliveryType").isEqualTo(store.deliveryType.name)
            .jsonPath("$.name").isEqualTo(store.name)
            .jsonPath("$.ownerName").isEqualTo(store.ownerName)
            .jsonPath("$.taxId").isEqualTo(store.taxId)
            .jsonPath("$.deliveryFee").isEqualTo(store.deliveryFee)
            .jsonPath("$.minimumOrderAmount").isEqualTo(store.minimumOrderAmount)
            .jsonPath("$.iconImageUrl").isEqualTo(store.iconImageUrl!!)
            .jsonPath("$.description").isEqualTo(store.description)
            .jsonPath("$.foodOrigin").isEqualTo(store.foodOrigin)
            .jsonPath("$.phoneNumber").isEqualTo(store.phoneNumber)
            .jsonPath("$.storeDetail.id").isEqualTo(storeDetail.id!!)
            .jsonPath("$.storeDetail.storeId").isEqualTo(storeDetail.storeId)
            .jsonPath("$.storeDetail.zipCode").isEqualTo(storeDetail.zipCode)
            .jsonPath("$.storeDetail.address").isEqualTo(storeDetail.address)
            .jsonPath("$.storeDetail.detailedAddress").isEqualTo(storeDetail.detailedAddress!!)
            .jsonPath("$.storeDetail.openHours").isEqualTo(storeDetail.openHours!!)
            .jsonPath("$.storeDetail.closedDay").isEqualTo(storeDetail.closedDay ?: "연중무휴")
            .jsonPath("$.storeImages[0].imageUrl").isEqualTo(storeImages[0].imageUrl)
            .jsonPath("$.storeImages[1].imageUrl").isEqualTo(storeImages[1].imageUrl)
            .jsonPath("$.storeImages[2].imageUrl").isEqualTo(storeImages[2].imageUrl)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("매장을 상세조회 할 수 있다.")
    @Test
    fun getStore() {
        val request = CreateStoreRequest(
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며,\n BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            storeDetail = CreateStoreDetailRequest(
                zipCode = "12345",
                address = "서울시 강남구 강남대로123길 12",
                detailedAddress = "1층 101호",
                openHours = "매일 오전 11:00 ~ 오후 11시 30분",
                closedDay = null,
            ),
            storeImage = CreateStoreImageRequest(
                imageUrls = listOf(
                    "image-url-1.jpg",
                    "image-url-2.jpg",
                    "image-url-3.jpg",
                )
            )
        )
        val now = LocalDateTime.now()
        val store = generateStore(request, now)
        val storeDetail = generateStoreDetail(store.id!!, request.storeDetail, now)
        val storeImages = generateStoreImage(store.id!!, request.storeImage, now)
        coEvery { useCase.getStore(any()) } returns store
        coEvery { storeDetailUseCase.getStoreDetailByStoreId(any()) } returns storeDetail
        coEvery { storeImageUseCase.getStoreImagesByStoreId(any()) } returns storeImages

        webTestClient.get().uri("/admin/v1/stores/${store.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(store.id!!)
            .jsonPath("$.categoryId").isEqualTo(store.categoryId)
            .jsonPath("$.deliveryType").isEqualTo(store.deliveryType.name)
            .jsonPath("$.name").isEqualTo(store.name)
            .jsonPath("$.ownerName").isEqualTo(store.ownerName)
            .jsonPath("$.taxId").isEqualTo(store.taxId)
            .jsonPath("$.deliveryFee").isEqualTo(store.deliveryFee)
            .jsonPath("$.minimumOrderAmount").isEqualTo(store.minimumOrderAmount)
            .jsonPath("$.iconImageUrl").isEqualTo(store.iconImageUrl!!)
            .jsonPath("$.description").isEqualTo(store.description)
            .jsonPath("$.foodOrigin").isEqualTo(store.foodOrigin)
            .jsonPath("$.phoneNumber").isEqualTo(store.phoneNumber)
            .jsonPath("$.storeDetail.id").isEqualTo(storeDetail.id!!)
            .jsonPath("$.storeDetail.storeId").isEqualTo(storeDetail.storeId)
            .jsonPath("$.storeDetail.zipCode").isEqualTo(storeDetail.zipCode)
            .jsonPath("$.storeDetail.address").isEqualTo(storeDetail.address)
            .jsonPath("$.storeDetail.detailedAddress").isEqualTo(storeDetail.detailedAddress!!)
            .jsonPath("$.storeDetail.openHours").isEqualTo(storeDetail.openHours!!)
            .jsonPath("$.storeDetail.closedDay").isEqualTo(storeDetail.closedDay ?: "연중무휴")
            .jsonPath("$.storeImages[0].imageUrl").isEqualTo(storeImages[0].imageUrl)
            .jsonPath("$.storeImages[1].imageUrl").isEqualTo(storeImages[1].imageUrl)
            .jsonPath("$.storeImages[2].imageUrl").isEqualTo(storeImages[2].imageUrl)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    private fun generateStore(request: CreateStoreRequest, now: LocalDateTime): Store {
        return Store(
            id = 1L,
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
            updatedAt = now,
        )
    }

    private fun generateStoreDetail(storeId: Long, request: CreateStoreDetailRequest, now: LocalDateTime): StoreDetail {
        return StoreDetail(
            id = 1L,
            storeId = storeId,
            zipCode = request.zipCode,
            address = request.address,
            detailedAddress = request.detailedAddress,
            openHours = request.openHours,
            closedDay = request.closedDay,
            createdAt = now
        )
    }

    private fun generateStoreImage(
        storeId: Long,
        request: CreateStoreImageRequest,
        now: LocalDateTime
    ): List<StoreImage> {
        return request.imageUrls.mapIndexed { index, url ->
            StoreImage(
                id = index.toLong() + 1,
                storeId = storeId,
                imageUrl = url,
                createdAt = now
            )
        }
    }
}