package hyuuny.fooddelivery.application.menu

import UpdateMenuRequest
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class UpdateMenuUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuRepository>()
    val useCase = MenuUseCase(repository)

    given("메뉴를 수정 할 때") {
        val menuId = 1L
        val now = LocalDateTime.now()
        val existingMenu = Menu(
            id = menuId,
            menuGroupId = 1L,
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )
        val request = UpdateMenuRequest(
            name = "후라이드 치킨",
            price = 20000,
            popularity = false,
            imageUrl = "chicken-image-url",
            description = "맛있는 양념치킨"
        )
        coEvery { repository.findById(any()) } returns existingMenu
        coEvery { repository.update(any()) } returns Unit

        `when`("금액을 0이상으로 입력하면") {
            useCase.updateMenu(menuId, request)
            then("메뉴를 수정 할 수 있다.") {
                coVerify { repository.update(any()) }
            }
        }

        `when`("금액을 0원 이하로 입력하면") {
            coEvery { repository.findById(any()) } returns existingMenu

            then("메뉴를 수정 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.updateMenu(
                        menuId, UpdateMenuRequest(
                            name = "후라이드 치킨",
                            price = 0,
                            popularity = false,
                            imageUrl = "chicken-image-url",
                            description = "맛있는 양념치킨"
                        )
                    )
                }
                ex.message shouldBe "금액은 0이상이여야 합니다."
            }
        }
    }
})