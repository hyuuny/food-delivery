package hyuuny.fooddelivery.presentation.api.v1.store

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.application.store.StoreDetailUseCase
import hyuuny.fooddelivery.application.store.StoreImageUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.MenuStatus.ON_SALE
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.store.StoreDetail
import hyuuny.fooddelivery.domain.store.StoreImage
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import java.time.LocalDateTime

class StoreApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: StoreUseCase

    @MockkBean
    private lateinit var storeDetailUseCase: StoreDetailUseCase

    @MockkBean
    private lateinit var storeImageUseCase: StoreImageUseCase

    @MockkBean
    private lateinit var menuGroupUseCase: MenuGroupUseCase

    @MockkBean
    private lateinit var menuUseCase: MenuUseCase


    @DisplayName("사용자는 매장의 메뉴들을 조회할 수 있다.")
    @Test
    fun getStore(): Unit = runBlocking {
        val storeId = 1L
        val store = generateStore(storeId)
        val storeDetail = generateStoreDetail(storeId)
        val storeImages = generateStoreImages(storeId)
        val menuGroups = generateMenuGroups(storeId)
        val menus = generateMenus(menuGroups)

        coEvery { useCase.getStore(storeId) } returns store
        coEvery { storeDetailUseCase.getStoreDetailByStoreId(storeId) } returns storeDetail
        coEvery { storeImageUseCase.getStoreImagesByStoreId(storeId) } returns storeImages
        coEvery { menuGroupUseCase.getAllByStoreId(storeId) } returns menuGroups
        coEvery { menuUseCase.getAllByMenuGroupIds(menuGroups.mapNotNull { it.id }) } returns menus

        webTestClient.get().uri("/api/v1/stores/$storeId")
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
            .jsonPath("$.storeDetail.closedDay").isEqualTo(storeDetail.closedDay!!)

            .jsonPath("$.storeImages.length()").isEqualTo(storeImages.size)
            .jsonPath("$.storeImages[0].id").isEqualTo(storeImages[0].id!!)
            .jsonPath("$.storeImages[0].storeId").isEqualTo(storeImages[0].storeId)
            .jsonPath("$.storeImages[0].imageUrl").isEqualTo(storeImages[0].imageUrl)

            .jsonPath("$.menuGroups.length()").isEqualTo(menuGroups.size)
            .jsonPath("$.menuGroups[0].id").isEqualTo(menuGroups[0].id!!)
            .jsonPath("$.menuGroups[0].storeId").isEqualTo(menuGroups[0].storeId)
            .jsonPath("$.menuGroups[0].name").isEqualTo(menuGroups[0].name)
            .jsonPath("$.menuGroups[0].priority").isEqualTo(menuGroups[0].priority)
            .jsonPath("$.menuGroups[0].description").isEqualTo(menuGroups[0].description!!)

            .jsonPath("$.menuGroups[0].menus.length()").isEqualTo(7)
            .jsonPath("$.menuGroups[0].menus[0].id").isEqualTo(menus[0].id!!)
            .jsonPath("$.menuGroups[0].menus[0].menuGroupId").isEqualTo(menus[0].menuGroupId)
            .jsonPath("$.menuGroups[0].menus[0].name").isEqualTo(menus[0].name)
            .jsonPath("$.menuGroups[0].menus[0].price").isEqualTo(menus[0].price)
            .jsonPath("$.menuGroups[0].menus[0].popularity").isEqualTo(menus[0].popularity)
            .jsonPath("$.menuGroups[0].menus[0].imageUrl").isEqualTo(menus[0].imageUrl!!)
            .jsonPath("$.menuGroups[0].menus[0].description").isEqualTo(menus[0].description!!)
    }

    @DisplayName("매장 목록을 조회할 수 있다.")
    @Test
    fun getStores() {
        val now = LocalDateTime.now()
        val stores = (1..5).map { id ->
            Store(
                id = id.toLong(),
                categoryId = 1L,
                deliveryType = DeliveryType.OUTSOURCING,
                name = "치킨집 $id",
                ownerName = "치킨집 주인 $id",
                taxId = "123-12-123$id",
                deliveryFee = 3000 + (id * 1000).toLong(),
                minimumOrderAmount = 15000 + (id * 5000).toLong(),
                iconImageUrl = "https://example.com/chicken-icon-$id.jpg",
                description = "치킨집 ${id}에서는 맛있는 치킨을 판매하고 있습니다. 신선한 식재료로 만든 맛있는 치킨을 맛보세요!",
                foodOrigin = "치킨집 ${id}의 치킨은 신선한 국내산 닭고기를 사용하여 만들어집니다.",
                phoneNumber = "02-1234-123$id",
                createdAt = now,
                updatedAt = now
            )
        }

        val menuGroups = stores.map { store ->
            val storeId = store.id!!
            MenuGroup(
                id = (storeId * 10 + 1),
                storeId = storeId,
                name = "치킨 메뉴 그룹 1",
                priority = 1,
                description = "치킨집 ${storeId}의 메뉴 그룹입니다. 다양한 맛있는 치킨 메뉴를 즐겨보세요!",
                createdAt = now,
                updatedAt = now
            )
        }

        val menus = menuGroups.flatMap { group ->
            (1..5).map { id ->
                val groupId = group.id!!
                Menu(
                    id = (groupId * 10 + id),
                    menuGroupId = groupId,
                    name = "치킨 메뉴 $id",
                    price = 10000 + (id * 1000).toLong(),
                    status = ON_SALE,
                    popularity = true,
                    imageUrl = "https://example.com/chicken-menu-image-$id.jpg",
                    description = "치킨집에서 판매하는 메뉴중 ${id}번째로 맛있는 치킨입니다. 맛있게 드세요!",
                    createdAt = now,
                    updatedAt = now
                )
            }
        }

        val sortedByDescending = stores.sortedByDescending { it.id }
        val page = PageImpl(sortedByDescending, PageRequest.of(0, 15), stores.size.toLong())
        coEvery { useCase.getStoresByApiCondition(any(), any()) } returns page
        coEvery { menuGroupUseCase.getAllByStoreIds(sortedByDescending.mapNotNull { it.id }) } returns menuGroups
        coEvery { menuUseCase.getAllByMenuGroupIds(menuGroups.mapNotNull { it.id }) } returns menus

        webTestClient.get().uri("/api/v1/stores?sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content.length()").isEqualTo(stores.size)
            .jsonPath("$.content[4].id").isEqualTo(stores[0].id!!)
            .jsonPath("$.content[4].categoryId").isEqualTo(stores[0].categoryId)
            .jsonPath("$.content[4].deliveryType").isEqualTo(stores[0].deliveryType.name)
            .jsonPath("$.content[4].name").isEqualTo(stores[0].name)
            .jsonPath("$.content[4].deliveryFee").isEqualTo(stores[0].deliveryFee)
            .jsonPath("$.content[4].minimumOrderAmount").isEqualTo(stores[0].minimumOrderAmount)
            .jsonPath("$.content[4].menuGroups.length()").isEqualTo(1)
            .jsonPath("$.content[4].menuGroups[0].id").isEqualTo(menuGroups[0].id!!)
            .jsonPath("$.content[4].menuGroups[0].storeId").isEqualTo(menuGroups[0].storeId)
            .jsonPath("$.content[4].menuGroups[0].menus.length()").isEqualTo(5)
            .jsonPath("$.content[4].menuGroups[0].menus[0].id").isEqualTo(menus[0].id!!)
            .jsonPath("$.content[4].menuGroups[0].menus[0].menuGroupId").isEqualTo(menus[0].menuGroupId)
            .jsonPath("$.content[4].menuGroups[0].menus[0].name").isEqualTo(menus[0].name)
            .jsonPath("$.content[4].menuGroups[0].menus[0].price").isEqualTo(menus[0].price)
            .jsonPath("$.content[4].menuGroups[0].menus[0].imageUrl").isEqualTo(menus[0].imageUrl!!)
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)
    }

    private fun generateStore(id: Long): Store {
        val now = LocalDateTime.now()
        return Store(
            id = id,
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url-5.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며, BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            createdAt = now,
            updatedAt = now
        )
    }

    private fun generateStoreDetail(storeId: Long): StoreDetail {
        val now = LocalDateTime.now()
        return StoreDetail(
            id = 1L,
            storeId = storeId,
            zipCode = "12345",
            address = "서울시 강남구 강남대로123길 12",
            detailedAddress = "1층 101호",
            openHours = "매일 오전 11:00 ~ 오후 11시 30분",
            closedDay = "연중무휴",
            createdAt = now,
        )
    }

    private fun generateStoreImages(storeId: Long): List<StoreImage> {
        val now = LocalDateTime.now()
        return listOf(
            StoreImage(1L, storeId, "image-url-1.jpg", now),
            StoreImage(2L, storeId, "image-url-2.jpg", now),
            StoreImage(3L, storeId, "image-url-3.jpg", now)
        )
    }

    private fun generateMenuGroups(storeId: Long): List<MenuGroup> {
        val now = LocalDateTime.now()
        return listOf(
            MenuGroup(1L, storeId, "추천메뉴", 1, "자신있게 추천드려요!", now, now),
            MenuGroup(2L, storeId, "매니아층을 위한", 2, null, now, now),
            MenuGroup(3L, storeId, "사이드 메뉴", 3, "여러가지 사이드 메뉴", now, now)
        )
    }

    private fun generateMenus(menuGroups: List<MenuGroup>): List<Menu> {
        val now = LocalDateTime.now()
        val menus = mutableListOf<Menu>()

        for (group in menuGroups) {
            menus.addAll(
                listOf(
                    Menu(1L, group.id!!, "상품1", 1000, ON_SALE, true, "image-url-1", "description", now, now),
                    Menu(2L, group.id!!, "상품2", 20000, ON_SALE, false, "image-url-2", "description", now, now),
                    Menu(3L, group.id!!, "상품3", 6000, ON_SALE, true, "image-url-3", "description", now, now),
                    Menu(4L, group.id!!, "상품4", 13000, ON_SALE, true, "image-url-4", "description", now, now),
                    Menu(5L, group.id!!, "상품5", 13000, ON_SALE, true, "image-url-5", "description", now, now),
                    Menu(6L, group.id!!, "상품6", 6000, ON_SALE, false, "image-url-6", "description", now, now),
                    Menu(7L, group.id!!, "상품7", 7000, ON_SALE, false, "image-url-7", "description", now, now)
                )
            )
        }
        return menus
    }

}