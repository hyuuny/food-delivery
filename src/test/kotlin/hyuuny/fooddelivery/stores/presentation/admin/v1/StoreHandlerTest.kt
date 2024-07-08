package hyuuny.fooddelivery.stores.presentation.admin.v1

import CreateStoreDetailRequest
import CreateStoreImageRequest
import CreateStoreRequest
import UpdateStoreRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.stores.application.StoreDetailUseCase
import hyuuny.fooddelivery.stores.application.StoreImageUseCase
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.stores.domain.StoreDetail
import hyuuny.fooddelivery.stores.domain.StoreImage
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class StoreHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: StoreUseCase

    @MockkBean
    private lateinit var storeDetailUseCase: StoreDetailUseCase

    @MockkBean
    private lateinit var storeImageUseCase: StoreImageUseCase

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
        val storeImages = generateStoreImage(store.id!!, request.storeImage!!, now)
        coEvery { useCase.createStore(any()) } returns store
        coEvery { storeDetailUseCase.getStoreDetailByStoreId(any()) } returns storeDetail
        coEvery { storeImageUseCase.getStoreImagesByStoreId(any()) } returns storeImages

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
        val storeImages = generateStoreImage(store.id!!, request.storeImage!!, now)
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

    @DisplayName("매장 목록을 조회할 수 있다.")
    @Test
    fun getStores() {
        val now = LocalDateTime.now()
        val firstStore = Store(
            id = 1L,
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며, BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            createdAt = now,
            updatedAt = now,
        )

        val secondStore = Store(
            id = 2L,
            categoryId = 2L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "백종원의 빽보이피자",
            ownerName = "나피자",
            taxId = "125-21-38923",
            deliveryFee = 0,
            minimumOrderAmount = 14000,
            iconImageUrl = "icon-image-url-1.jpg",
            description = "안녕하세요. 백종원이 빽보이피자입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립니다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "02-1231-2308",
            createdAt = now.plusHours(1),
            updatedAt = now.plusHours(1),
        )

        val thirdStore = Store(
            id = 3L,
            categoryId = 2L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "백종원의 빽보이피자 2",
            ownerName = "다피자",
            taxId = "125-21-09283",
            deliveryFee = 1000,
            minimumOrderAmount = 13000,
            iconImageUrl = "icon-image-url-2.jpg",
            description = "안녕하세요. 백종원이 빽보이피자 2입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "02-1938-2973",
            createdAt = now.plusHours(3),
            updatedAt = now.plusHours(3),
        )

        val fourthStore = Store(
            id = 4L,
            categoryId = 2L,
            deliveryType = DeliveryType.TAKE_OUT,
            name = "가종원의 빽보이피자",
            ownerName = "가피자",
            taxId = "125-21-12397",
            deliveryFee = 3000,
            minimumOrderAmount = 15000,
            description = "안녕하세요. 가종원이 빽보이피자입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립니다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "02-1726-2397",
            createdAt = now.plusHours(4),
            updatedAt = now.plusHours(4),
        )

        val fifthStore = Store(
            id = 5L,
            categoryId = 2L,
            deliveryType = DeliveryType.SELF,
            name = "가종원의 빽보이피자 2",
            ownerName = "기피자",
            taxId = "125-21-38723",
            deliveryFee = 2500,
            minimumOrderAmount = 16000,
            description = "안녕하세요. 가종원이 빽보이피자 2입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립니다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "070-9278-8765",
            createdAt = now.plusHours(5),
            updatedAt = now.plusHours(5),
        )
        val stores = listOf(firstStore, secondStore, thirdStore, fourthStore, fifthStore).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(stores, pageable, stores.size.toLong())
        coEvery { useCase.getStoresByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/stores?sort:id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(fifthStore.id!!)
            .jsonPath("$.content[0].categoryId").isEqualTo(fifthStore.categoryId)
            .jsonPath("$.content[0].deliveryType").isEqualTo(fifthStore.deliveryType.name)
            .jsonPath("$.content[0].name").isEqualTo(fifthStore.name)
            .jsonPath("$.content[0].ownerName").isEqualTo(fifthStore.ownerName)
            .jsonPath("$.content[0].taxId").isEqualTo(fifthStore.taxId)
            .jsonPath("$.content[0].deliveryFee").isEqualTo(fifthStore.deliveryFee)
            .jsonPath("$.content[0].phoneNumber").isEqualTo(fifthStore.phoneNumber)
            .jsonPath("$.content[0].createdAt").exists()

            .jsonPath("$.content[1].id").isEqualTo(fourthStore.id!!)
            .jsonPath("$.content[1].categoryId").isEqualTo(fourthStore.categoryId)
            .jsonPath("$.content[1].deliveryType").isEqualTo(fourthStore.deliveryType.name)
            .jsonPath("$.content[1].name").isEqualTo(fourthStore.name)
            .jsonPath("$.content[1].ownerName").isEqualTo(fourthStore.ownerName)
            .jsonPath("$.content[1].taxId").isEqualTo(fourthStore.taxId)
            .jsonPath("$.content[1].deliveryFee").isEqualTo(fourthStore.deliveryFee)
            .jsonPath("$.content[1].phoneNumber").isEqualTo(fourthStore.phoneNumber)
            .jsonPath("$.content[1].createdAt").exists()

            .jsonPath("$.content[2].id").isEqualTo(thirdStore.id!!)
            .jsonPath("$.content[2].categoryId").isEqualTo(thirdStore.categoryId)
            .jsonPath("$.content[2].deliveryType").isEqualTo(thirdStore.deliveryType.name)
            .jsonPath("$.content[2].name").isEqualTo(thirdStore.name)
            .jsonPath("$.content[2].ownerName").isEqualTo(thirdStore.ownerName)
            .jsonPath("$.content[2].taxId").isEqualTo(thirdStore.taxId)
            .jsonPath("$.content[2].deliveryFee").isEqualTo(thirdStore.deliveryFee)
            .jsonPath("$.content[2].iconImageUrl").isEqualTo(thirdStore.iconImageUrl!!)
            .jsonPath("$.content[2].phoneNumber").isEqualTo(thirdStore.phoneNumber)
            .jsonPath("$.content[2].createdAt").exists()

            .jsonPath("$.content[3].id").isEqualTo(secondStore.id!!)
            .jsonPath("$.content[3].categoryId").isEqualTo(secondStore.categoryId)
            .jsonPath("$.content[3].deliveryType").isEqualTo(secondStore.deliveryType.name)
            .jsonPath("$.content[3].name").isEqualTo(secondStore.name)
            .jsonPath("$.content[3].ownerName").isEqualTo(secondStore.ownerName)
            .jsonPath("$.content[3].taxId").isEqualTo(secondStore.taxId)
            .jsonPath("$.content[3].deliveryFee").isEqualTo(secondStore.deliveryFee)
            .jsonPath("$.content[3].iconImageUrl").isEqualTo(secondStore.iconImageUrl!!)
            .jsonPath("$.content[3].phoneNumber").isEqualTo(secondStore.phoneNumber)
            .jsonPath("$.content[3].createdAt").exists()

            .jsonPath("$.content[4].id").isEqualTo(firstStore.id!!)
            .jsonPath("$.content[4].categoryId").isEqualTo(firstStore.categoryId)
            .jsonPath("$.content[4].deliveryType").isEqualTo(firstStore.deliveryType.name)
            .jsonPath("$.content[4].name").isEqualTo(firstStore.name)
            .jsonPath("$.content[4].ownerName").isEqualTo(firstStore.ownerName)
            .jsonPath("$.content[4].taxId").isEqualTo(firstStore.taxId)
            .jsonPath("$.content[4].deliveryFee").isEqualTo(firstStore.deliveryFee)
            .jsonPath("$.content[4].iconImageUrl").isEqualTo(firstStore.iconImageUrl!!)
            .jsonPath("$.content[4].phoneNumber").isEqualTo(firstStore.phoneNumber)
            .jsonPath("$.content[4].createdAt").exists()

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

    @DisplayName("매장을 수정할 수 있다.")
    @Test
    fun updateStore() {
        val storeId = 1L
        val now = LocalDateTime.now()
        val request = UpdateStoreRequest(
            categoryId = 2L,
            deliveryType = DeliveryType.SELF,
            name = "가종원의 빽보이피자 2",
            ownerName = "기피자",
            taxId = "125-21-38723",
            deliveryFee = 2500,
            minimumOrderAmount = 16000,
            iconImageUrl = null,
            description = "안녕하세요. 가종원이 빽보이피자 2입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립니다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "070-9278-8765",
            storeDetail = CreateStoreDetailRequest(
                zipCode = "12345",
                address = "서울시 강남구 강남대로123길 12",
                detailedAddress = "1층 101호",
                openHours = "매일 오전 11:00 ~ 오후 11시 30분",
                closedDay = null,
            ),
            storeImage = null
        )

        val store = Store(
            id = storeId,
            categoryId = 2L,
            deliveryType = DeliveryType.SELF,
            name = "가종원의 빽보이피자 2",
            ownerName = "기피자",
            taxId = "125-21-38723",
            deliveryFee = 2500,
            minimumOrderAmount = 16000,
            iconImageUrl = null,
            description = "안녕하세요. 가종원이 빽보이피자 2입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립니다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "070-9278-8765",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { useCase.updateStore(any(), any()) } returns store

        val storeDetail = StoreDetail(
            id = 1,
            storeId = store.id!!,
            zipCode = "12345",
            address = "서울시 강남구 강남대로123길 12",
            detailedAddress = "1층 101호",
            openHours = "매일 오전 11:00 ~ 오후 11시 30분",
            closedDay = null,
            createdAt = now,
        )
        coEvery { storeDetailUseCase.getStoreDetailByStoreId(any()) } returns storeDetail

        val storeImages = listOf(
            StoreImage(id = 1, storeId = store.id!!, imageUrl = "image-url-1.jpg", createdAt = now),
            StoreImage(id = 2, storeId = store.id!!, imageUrl = "image-url-2.jpg", createdAt = now),
            StoreImage(id = 3, storeId = store.id!!, imageUrl = "image-url-3.jpg", createdAt = now),
        )
        coEvery { storeImageUseCase.getStoreImagesByStoreId(any()) } returns storeImages

        webTestClient.put().uri("/admin/v1/stores/${storeId}")
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
            .jsonPath("$.iconImageUrl").doesNotExist()
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

    @DisplayName("매장을 삭제할 수 있다.")
    @Test
    fun deleteStore() {
        val storeId = 1L
        coEvery { useCase.deleteStore(storeId) } returns Unit

        webTestClient.delete().uri("/admin/v1/stores/${storeId}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
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
