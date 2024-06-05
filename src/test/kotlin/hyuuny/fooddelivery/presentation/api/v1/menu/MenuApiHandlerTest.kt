package hyuuny.fooddelivery.presentation.api.v1.menu

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import hyuuny.fooddelivery.presentation.api.v1.menu.response.MenuResponse
import hyuuny.fooddelivery.presentation.api.v1.menu.response.OptionGroupResponse
import hyuuny.fooddelivery.presentation.api.v1.menu.response.OptionResponse
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class MenuApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var menuUseCase: MenuUseCase

    @MockkBean
    private lateinit var optionGroupUseCase: OptionGroupUseCase

    @MockkBean
    private lateinit var optionUseCase: OptionUseCase

    @DisplayName("메뉴와 그에 속하는 옵션그룹, 옵션들을 조회할 수 있다.")
    @Test
    fun getMenu(): Unit = runBlocking {
        val menuId = 1L
        val menu = generateMenu(menuId)
        val optionGroups = generateOptionGroups(menuId)
        val optionGroupIds = optionGroups.map { it.id!! }
        val options = generateOptions(optionGroupIds)

        coEvery { menuUseCase.getMenu(menuId) } returns menu
        coEvery { optionGroupUseCase.getAllByMenuId(menuId) } returns optionGroups
        coEvery { optionUseCase.getAllByOptionGroupIds(optionGroupIds) } returns options

        val expectedResponse = MenuResponse.from(
            menu,
            optionGroups.map { group ->
                val filteringOptions = options.filter { it.optionGroupId == group.id }
                    .map { OptionResponse.from(it) }
                OptionGroupResponse.from(group, filteringOptions)
            }
        )

        webTestClient.get().uri("/api/v1/menus/$menuId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.menuGroupId").isEqualTo(expectedResponse.menuGroupId)
            .jsonPath("$.name").isEqualTo(expectedResponse.name)
            .jsonPath("$.price").isEqualTo(expectedResponse.price)
            .jsonPath("$.popularity").isEqualTo(expectedResponse.popularity)
            .jsonPath("$.imageUrl").isEqualTo(expectedResponse.imageUrl!!)
            .jsonPath("$.description").isEqualTo(expectedResponse.description!!)
            .jsonPath("$.optionGroups").isArray
            .jsonPath("$.optionGroups[0].id").isEqualTo(expectedResponse.optionGroups[0].id)
            .jsonPath("$.optionGroups[0].name").isEqualTo(expectedResponse.optionGroups[0].name)
            .jsonPath("$.optionGroups[0].options").isArray
            .jsonPath("$.optionGroups[0].options.length()").isEqualTo(8)
            .jsonPath("$.optionGroups[0].options[0].id").isEqualTo(expectedResponse.optionGroups[0].options[0].id)
    }

    private fun generateMenu(id: Long): Menu {
        val now = LocalDateTime.now()
        return Menu(
            id = id,
            menuGroupId = 1L,
            name = "싸이버거 세트",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )
    }

    private fun generateOptionGroups(menuId: Long): List<OptionGroup> {
        val now = LocalDateTime.now()
        return listOf(
            OptionGroup(
                id = 1L,
                menuId = menuId,
                name = "토핑",
                required = true,
                priority = 1,
                createdAt = now,
                updatedAt = now
            ),
            OptionGroup(
                id = 2L,
                menuId = menuId,
                name = "세트 사이드메뉴 변경",
                required = false,
                priority = 2,
                createdAt = now,
                updatedAt = now
            ),
            OptionGroup(
                id = 3L,
                menuId = menuId,
                name = "음료 선택",
                required = true,
                priority = 3,
                createdAt = now,
                updatedAt = now
            ),
            OptionGroup(
                id = 4L,
                menuId = menuId,
                name = "사이드 추가",
                required = false,
                priority = 4,
                createdAt = now,
                updatedAt = now
            ),
            OptionGroup(
                id = 5L,
                menuId = menuId,
                name = "소스 추가",
                required = false,
                priority = 5,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    private fun generateOptions(groupIds: List<Long>): List<Option> {
        val now = LocalDateTime.now()
        val options = mutableListOf<Option>()
        for (groupId in groupIds) {
            for (i in 1..8) { // 각 그룹당 8개의 옵션 생성
                options.add(
                    Option(
                        id = i.toLong(),
                        optionGroupId = groupId,
                        name = "Option $i",
                        price = (i * 1000).toLong(),
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }
        }
        return options
    }

}