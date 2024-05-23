package hyuuny.fooddelivery.presentation.admin.v1.menuoption

import CreateMenuOptionRequest
import UpdateMenuOptionRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menuoption.MenuOptionUseCase
import hyuuny.fooddelivery.domain.menuoption.MenuOption
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    @DisplayName("메뉴옵션 목록을 조회할 수 있다.")
    @Test
    fun getMenuOptions() {
        val menuGroupId = 1L
        val now = LocalDateTime.now()
        val firstOption = MenuOption(
            id = 1L,
            menuGroupId = menuGroupId,
            name = "후라이드 + 양념",
            price = 1000,
            createdAt = now,
            updatedAt = now
        )
        val secondOption = MenuOption(
            id = 2L,
            menuGroupId = menuGroupId,
            name = "후라이드 + 간장",
            price = 1000,
            createdAt = now,
            updatedAt = now
        )
        val thirdOption = MenuOption(
            id = 3L,
            menuGroupId = menuGroupId,
            name = "양념 + 간장",
            price = 2000,
            createdAt = now,
            updatedAt = now
        )
        val fourthOption = MenuOption(
            id = 4L,
            menuGroupId = menuGroupId,
            name = "후라이드 + 크리미언",
            price = 2500,
            createdAt = now,
            updatedAt = now
        )
        val menuOptions = listOf(firstOption, secondOption, thirdOption, fourthOption).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id"))
        val page = PageImpl(menuOptions, pageable, menuOptions.size.toLong())
        coEvery { useCase.getMenuOptions(any(), any()) } returns page

        webTestClient.get().uri("/v1/menu-options?menu-group-id=&name=&sort=id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(fourthOption.id!!)
            .jsonPath("$.content[0].menuGroupId").isEqualTo(fourthOption.menuGroupId)
            .jsonPath("$.content[0].name").isEqualTo(fourthOption.name)
            .jsonPath("$.content[0].price").isEqualTo(fourthOption.price)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(thirdOption.id!!)
            .jsonPath("$.content[1].menuGroupId").isEqualTo(thirdOption.menuGroupId)
            .jsonPath("$.content[1].name").isEqualTo(thirdOption.name)
            .jsonPath("$.content[1].price").isEqualTo(thirdOption.price)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(secondOption.id!!)
            .jsonPath("$.content[2].menuGroupId").isEqualTo(secondOption.menuGroupId)
            .jsonPath("$.content[2].name").isEqualTo(secondOption.name)
            .jsonPath("$.content[2].price").isEqualTo(secondOption.price)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.content[3].id").isEqualTo(firstOption.id!!)
            .jsonPath("$.content[3].menuGroupId").isEqualTo(firstOption.menuGroupId)
            .jsonPath("$.content[3].name").isEqualTo(firstOption.name)
            .jsonPath("$.content[3].price").isEqualTo(firstOption.price)
            .jsonPath("$.content[3].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
    }

    @DisplayName("메뉴옵션을 가격은 오름차순으로, 아이디는 내림차순으로 목록을 조회할 수 있다.")
    @Test
    fun getMenuOptions_sort_price_asc() {
        val menuGroupId = 1L
        val now = LocalDateTime.now()
        val firstOption = MenuOption(
            id = 1L,
            menuGroupId = menuGroupId,
            name = "후라이드 + 양념",
            price = 2000,
            createdAt = now,
            updatedAt = now
        )
        val secondOption = MenuOption(
            id = 2L,
            menuGroupId = menuGroupId,
            name = "후라이드 + 간장",
            price = 3000,
            createdAt = now,
            updatedAt = now
        )
        val thirdOption = MenuOption(
            id = 3L,
            menuGroupId = menuGroupId,
            name = "양념 + 간장",
            price = 2000,
            createdAt = now,
            updatedAt = now
        )
        val fourthOption = MenuOption(
            id = 4L,
            menuGroupId = menuGroupId,
            name = "후라이드 + 크리미언",
            price = 2500,
            createdAt = now,
            updatedAt = now
        )
        val menuOptions = listOf(firstOption, secondOption, thirdOption, fourthOption)
            .sortedWith(compareBy<MenuOption> { it.price }.thenByDescending { it.id })

        val firstSort = Sort.Order(Sort.Direction.ASC, "price")
        val secondSort = Sort.Order(Sort.Direction.DESC, "id")
        val sort = Sort.by(listOf(firstSort, secondSort))

        val pageable = PageRequest.of(0, 15, sort)
        val page = PageImpl(menuOptions, pageable, menuOptions.size.toLong())
        coEvery { useCase.getMenuOptions(any(), any()) } returns page

        webTestClient.get().uri("/v1/menu-options?menu-group-id=&name=&sort=price:asc,id:desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(thirdOption.id!!)
            .jsonPath("$.content[0].menuGroupId").isEqualTo(thirdOption.menuGroupId)
            .jsonPath("$.content[0].name").isEqualTo(thirdOption.name)
            .jsonPath("$.content[0].price").isEqualTo(thirdOption.price)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(firstOption.id!!)
            .jsonPath("$.content[1].menuGroupId").isEqualTo(firstOption.menuGroupId)
            .jsonPath("$.content[1].name").isEqualTo(firstOption.name)
            .jsonPath("$.content[1].price").isEqualTo(firstOption.price)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.content[2].id").isEqualTo(fourthOption.id!!)
            .jsonPath("$.content[2].menuGroupId").isEqualTo(fourthOption.menuGroupId)
            .jsonPath("$.content[2].name").isEqualTo(fourthOption.name)
            .jsonPath("$.content[2].price").isEqualTo(fourthOption.price)
            .jsonPath("$.content[2].createdAt").exists()
            .jsonPath("$.content[3].id").isEqualTo(secondOption.id!!)
            .jsonPath("$.content[3].menuGroupId").isEqualTo(secondOption.menuGroupId)
            .jsonPath("$.content[3].name").isEqualTo(secondOption.name)
            .jsonPath("$.content[3].price").isEqualTo(secondOption.price)
            .jsonPath("$.content[3].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
    }

    @DisplayName("메뉴옵션의 정보를 변경할 수 있다.")
    @Test
    fun updateMenuOption() {
        coEvery { menuGroupRepository.existsById(any()) } returns true
        val request = UpdateMenuOptionRequest(
            name = "불닭 + 청양마요",
            price = 3000,
        )
        coEvery { useCase.updateMenuOption(any(), any()) } returns Unit

        webTestClient.put().uri("/v1/menu-groups/1/menu-options/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
    }

    @DisplayName("존재하지 않는 메뉴그룹의 옵션 정보는 변경할 수 있다.")
    @Test
    fun updateMenuOption_notFound() {
        coEvery { menuGroupRepository.existsById(any()) } throws ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 메뉴그룹입니다."
        )

        val request = UpdateMenuOptionRequest(
            name = "불닭 + 청양마요",
            price = 3000,
        )
        coEvery { useCase.updateMenuOption(any(), any()) } returns Unit

        webTestClient.put().uri("/v1/menu-groups/1/menu-options/1")
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