package hyuuny.fooddelivery.presentation.admin.v1.menu

import ChangeMenuStatusRequest
import CreateMenuRequest
import UpdateMenuRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime


class MenuHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: MenuUseCase

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    fun createMenu() {
        val request = CreateMenuRequest(
            menuGroupId = 1L,
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )
        val menu = generateMenu(request)
        coEvery { useCase.createMenu(request) } returns menu

        webTestClient.post().uri("/admin/v1/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menu.id!!)
            .jsonPath("$.menuGroupId").isEqualTo(menu.menuGroupId)
            .jsonPath("$.name").isEqualTo(menu.name)
            .jsonPath("$.price").isEqualTo(menu.price)
            .jsonPath("$.status").isEqualTo(menu.status.name)
            .jsonPath("$.popularity").isEqualTo(menu.popularity)
            .jsonPath("$.imageUrl").isEqualTo(menu.imageUrl!!)
            .jsonPath("$.description").isEqualTo(menu.description!!)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("메뉴 가격이 0이하이면 등록할 수 없다.")
    @Test
    fun createMenu_invalidPrice() {
        val request = CreateMenuRequest(
            menuGroupId = 1L,
            name = "싸이버거",
            price = 0,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )

        webTestClient.post().uri("/admin/v1/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().is5xxServerError
    }

    @DisplayName("메뉴를 상세조회할 수 있다.")
    @Test
    fun getMenu() {
        val request = CreateMenuRequest(
            menuGroupId = 1L,
            name = "싸이버거",
            price = 5000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )
        val menu = generateMenu(request)
        coEvery { useCase.getMenu(any()) } returns menu

        webTestClient.get().uri("/admin/v1/menus/${menu.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menu.id!!)
            .jsonPath("$.menuGroupId").isEqualTo(menu.menuGroupId)
            .jsonPath("$.name").isEqualTo(menu.name)
            .jsonPath("$.price").isEqualTo(menu.price)
            .jsonPath("$.status").isEqualTo(menu.status.name)
            .jsonPath("$.popularity").isEqualTo(menu.popularity)
            .jsonPath("$.imageUrl").isEqualTo(menu.imageUrl!!)
            .jsonPath("$.description").isEqualTo(menu.description!!)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("잘못된 메뉴 아이디로 조회할 수 없다.")
    @Test
    fun getMenu_notFound() {
        webTestClient.get().uri("/admin/v1/menus/${0}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError
    }

    @DisplayName("메뉴의 정보를 변경할 수 있다.")
    @Test
    fun updateMenu() {
        val request = UpdateMenuRequest(
            name = "후라이드 치킨",
            price = 20000,
            popularity = false,
            imageUrl = "chicken-image-url",
            description = "맛있는 양념치킨"
        )
        coEvery { useCase.updateMenu(any(), request) } returns Unit

        webTestClient.put().uri("/admin/v1/menus/${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("메뉴의 상태를 변경할 수 있다.")
    @Test
    fun changeMenuStatus() {
        val request = ChangeMenuStatusRequest(
            status = MenuStatus.SOLD_OUT
        )
        coEvery { useCase.changeMenuStatus(any(), request) } returns Unit

        webTestClient.patch().uri("/admin/v1/menus/change-status/${1}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("메뉴를 삭제할 수 있다.")
    @Test
    fun deleteMenu() {
        coEvery { useCase.deleteMenu(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/menus/${1}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("메뉴 목록을 불러올 수 있다.")
    @Test
    fun getMenus() {
        val now = LocalDateTime.now()
        val cyburger = Menu(
            id = 1L,
            menuGroupId = 1L,
            name = "싸이버거",
            price = 5000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )

        val hotDog = Menu(
            id = 2,
            menuGroupId = 1L,
            name = "핫도그",
            price = 3000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "hotdog-image-url",
            description = "[인기메뉴]닭고기로 만든 핫도그",
            createdAt = now,
            updatedAt = now
        )
        val menus = listOf(cyburger, hotDog).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"))
        val page = PageImpl(menus, pageable, menus.size.toLong())

        coEvery { useCase.getMenusByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/menus?name=&status=&popularity=&sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(2)
            .jsonPath("$.content[0].menuGroupId").isEqualTo(1)
            .jsonPath("$.content[0].name").isEqualTo("핫도그")
            .jsonPath("$.content[0].price").isEqualTo(3000)
            .jsonPath("$.content[0].status").isEqualTo("ON_SALE")
            .jsonPath("$.content[0].popularity").isEqualTo(true)
            .jsonPath("$.content[0].imageUrl").isEqualTo("hotdog-image-url")
            .jsonPath("$.content[0].description").isEqualTo("[인기메뉴]닭고기로 만든 핫도그")
            .jsonPath("$.content[1].id").isEqualTo(1)
            .jsonPath("$.content[1].menuGroupId").isEqualTo(1)
            .jsonPath("$.content[1].name").isEqualTo("싸이버거")
            .jsonPath("$.content[1].price").isEqualTo(5000)
            .jsonPath("$.content[1].status").isEqualTo("ON_SALE")
            .jsonPath("$.content[1].popularity").isEqualTo(true)
            .jsonPath("$.content[1].imageUrl").isEqualTo("cyburger-image-url")
            .jsonPath("$.content[1].description").isEqualTo("[베스트]닭다리살")
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(2)
    }

    @DisplayName("메뉴 목록을 아이디 기준으로 오름차순 정렬할 수 있다.")
    @Test
    fun getMenus_sort_id_asc() {
        val now = LocalDateTime.now()
        val cyburger = Menu(
            id = 1L,
            menuGroupId = 1L,
            name = "싸이버거",
            price = 5000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )

        val hotDog = Menu(
            id = 2,
            menuGroupId = 1L,
            name = "핫도그",
            price = 3000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "hotdog-image-url",
            description = "[인기메뉴]닭고기로 만든 핫도그",
            createdAt = now,
            updatedAt = now
        )
        val menus = listOf(cyburger, hotDog)

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.ASC, "id"))
        val page = PageImpl(menus, pageable, menus.size.toLong())

        coEvery { useCase.getMenusByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/menus?name=&status=&popularity=&sort=id:asc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(1)
            .jsonPath("$.content[0].menuGroupId").isEqualTo(1)
            .jsonPath("$.content[0].name").isEqualTo("싸이버거")
            .jsonPath("$.content[0].price").isEqualTo(5000)
            .jsonPath("$.content[0].status").isEqualTo("ON_SALE")
            .jsonPath("$.content[0].popularity").isEqualTo(true)
            .jsonPath("$.content[0].imageUrl").isEqualTo("cyburger-image-url")
            .jsonPath("$.content[0].description").isEqualTo("[베스트]닭다리살")
            .jsonPath("$.content[1].id").isEqualTo(2)
            .jsonPath("$.content[1].menuGroupId").isEqualTo(1)
            .jsonPath("$.content[1].name").isEqualTo("핫도그")
            .jsonPath("$.content[1].price").isEqualTo(3000)
            .jsonPath("$.content[1].status").isEqualTo("ON_SALE")
            .jsonPath("$.content[1].popularity").isEqualTo(true)
            .jsonPath("$.content[1].imageUrl").isEqualTo("hotdog-image-url")
            .jsonPath("$.content[1].description").isEqualTo("[인기메뉴]닭고기로 만든 핫도그")
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(2)
    }

    private fun generateMenu(request: CreateMenuRequest): Menu {
        val now = LocalDateTime.now()
        return Menu(
            id = 1,
            menuGroupId = 1,
            name = request.name,
            price = request.price,
            status = request.status,
            popularity = request.popularity,
            imageUrl = request.imageUrl,
            description = request.description,
            createdAt = now,
            updatedAt = now,
        )
    }

}