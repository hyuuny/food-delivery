package hyuuny.fooddelivery.menus.application

import ChangeMenuStatusRequest
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.menus.infrastructure.MenuRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class ChangeMenuStatusUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuRepository>()
    val useCase = MenuUseCase(repository)

    given("메뉴의 상태를 변경 할 때") {
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
        val request = ChangeMenuStatusRequest(
            status = MenuStatus.SOLD_OUT
        )
        coEvery { repository.findById(any()) } returns existingMenu
        coEvery { repository.updateMenuStatus(any()) } returns Unit

        `when`("존재하는 메뉴라면") {
            useCase.changeMenuStatus(menuId, request)

            then("메뉴의 상태를 변경 할 수 있다.") {
                coVerify { repository.updateMenuStatus(any()) }
            }
        }

        `when`("존재하지 않는 메뉴라면") {
            coEvery { repository.findById(any()) } returns null

            then("메뉴의 상태를 변경 할 수 없다.") {
                val notExistId = 0L
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeMenuStatus(notExistId, ChangeMenuStatusRequest(MenuStatus.SOLD_OUT))
                }
                ex.message shouldBe "${notExistId}번 메뉴를 찾을 수 없습니다."
            }
        }
    }
})
