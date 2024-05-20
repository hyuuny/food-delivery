package hyuuny.fooddelivery.presentation.admin.v1.menugroup

import CreateMenuGroupRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

class MenuGroupHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: MenuGroupUseCase

    @MockkBean
    private lateinit var menuRepository: MenuRepository

    @DisplayName("메뉴의 메뉴그룹을 등록할 수 있다.")
    @Test
    fun createMenuGroup() {
        coEvery { menuRepository.existsById(any()) } returns true
        val request = CreateMenuGroupRequest(
            menuId = 1L,
            name = "치킨세트",
            required = true
        )
        val menuGroup = generateMenuGroup(request)
        coEvery { useCase.createMenuGroup(request) } returns menuGroup

        webTestClient.post().uri("/menus/${request.menuId}/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(menuGroup.id!!)
            .jsonPath("$.menuId").isEqualTo(menuGroup.menuId)
            .jsonPath("$.name").isEqualTo(menuGroup.name)
            .jsonPath("$.required").isEqualTo(menuGroup.required)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists()
    }

    @DisplayName("존재하지 메뉴의 메뉴그룹은 등록할 수 없다.")
    @Test
    fun createMenuGroup_notFound() {
        coEvery { menuRepository.existsById(any()) } throws ResponseStatusException(NOT_FOUND, "존재하지 않는 메뉴입니다.")
        val request = CreateMenuGroupRequest(
            menuId = 0L,
            name = "치킨세트",
            required = true
        )

        webTestClient.post().uri("/menus/${request.menuId}/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    private fun generateMenuGroup(request: CreateMenuGroupRequest): MenuGroup {
        val now = LocalDateTime.now()
        return MenuGroup(
            id = 1L,
            menuId = request.menuId,
            name = request.name,
            required = request.required,
            createdAt = now,
            updatedAt = now
        )
    }
}
