package hyuuny.fooddelivery.handler

import ChangeMenuStatusRequest
import CreateMenuRequest
import UpdateMenuRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.menu.MenuStatus
import hyuuny.fooddelivery.domain.menu.Price
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime


class MenuHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: MenuUseCase

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    fun createMenu() {
        val request = CreateMenuRequest(
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )
        val menu = generateMenu(request)
        coEvery { useCase.createMenu(request) } returns menu

        webTestClient.post().uri("/v1/menus")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menu.id!!)
            .jsonPath("$.name").isEqualTo(menu.name)
            .jsonPath("$.price").isEqualTo(menu.price.value)
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
            name = "싸이버거",
            price = 0,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )

        webTestClient.post().uri("/v1/menus")
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
            name = "싸이버거",
            price = 5000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )
        val menu = generateMenu(request)
        coEvery { useCase.getMenu(any()) } returns menu

        webTestClient.get().uri("/v1/menus/${menu.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menu.id!!)
            .jsonPath("$.name").isEqualTo(menu.name)
            .jsonPath("$.price").isEqualTo(menu.price.value)
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
        webTestClient.get().uri("/v1/menus/${0}")
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

        webTestClient.put().uri("/v1/menus/${1}")
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

        webTestClient.patch().uri("/v1/menus/change-status/${1}")
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

        webTestClient.delete().uri("/v1/menus/${1}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    private fun generateMenu(request: CreateMenuRequest): Menu {
        val now = LocalDateTime.now()
        return Menu(
            id = 1,
            name = request.name,
            price = Price(request.price),
            status = request.status,
            popularity = request.popularity,
            imageUrl = request.imageUrl,
            description = request.description,
            createdAt = now,
            updatedAt = now,
        )
    }
}