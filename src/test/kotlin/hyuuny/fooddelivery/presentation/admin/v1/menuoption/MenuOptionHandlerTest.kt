package hyuuny.fooddelivery.presentation.admin.v1.menuoption

import CreateMenuOptionRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menuoption.MenuOptionUseCase
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

class MenuOptionHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: MenuOptionUseCase

    @MockkBean
    private lateinit var menuGroupRepository: MenuGroupRepository

    @DisplayName("메뉴그룹에 옵션을 등록할 수 있다.")
    @Test
    fun createMenuOption() {
        coEvery { menuGroupRepository.existsById(any()) } returns true
        val request = CreateMenuOptionRequest(
            menuGroupId = 1L,
            name = "후라이드 + 양념",
            price = 1000,
        )
        val menuOption = generateMenuOption(request)
        coEvery { useCase.createMenuOption(any()) } returns menuOption

        webTestClient.post().uri("/v1/menu-groups/${request.menuGroupId}/menu-options")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menuOption.id!!)
            .jsonPath("$.menuGroupId").isEqualTo(menuOption.menuGroupId)
            .jsonPath("$.name").isEqualTo(menuOption.name)
            .jsonPath("$.price").isEqualTo(menuOption.price)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("존재하지 않는 메뉴그룹에 옵션을 등록할 수 없다.")
    @Test
    fun createMenuOption_notFound() {
        coEvery { menuGroupRepository.existsById(any()) } throws ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 메뉴그룹입니다."
        )
        val request = CreateMenuOptionRequest(
            menuGroupId = 0L,
            name = "후라이드 + 양념",
            price = 1000,
        )

        webTestClient.post().uri("/v1/menu-groups/${request.menuGroupId}/menu-options")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    private fun generateMenuOption(request: CreateMenuOptionRequest): MenuOption {
        val now = LocalDateTime.now()
        return MenuOption(
            id = 1L,
            menuGroupId = request.menuGroupId,
            name = request.name,
            price = request.price,
            createdAt = now,
            updatedAt = now
        )
    }
}