package hyuuny.fooddelivery.menus.application

import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.menugroups.application.MenuGroupUseCase
import hyuuny.fooddelivery.menugroups.domain.MenuGroup
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.menus.infrastructure.MenuRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class ChangeMenuGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuRepository>()
    val useCase = MenuUseCase(repository)
    val menuGroupUseCase = mockk<MenuGroupUseCase>()

    given("메뉴의 메뉴그룹을 변경 할 때") {
        val menuGroupId = 19L
        val menuId = 1L

        val now = LocalDateTime.now()
        val menuGroup = MenuGroup(
            id = menuGroupId,
            storeId = 1L,
            name = "변경된 추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now
        )

        val menu = Menu(
            id = menuId,
            menuGroupId = menuGroup.id!!,
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )
        coEvery { menuGroupUseCase.getMenuGroup(any()) } returns menuGroup
        coEvery { repository.findById(any()) } returns menu
        coEvery { repository.updateMenuGroupId(any()) } returns Unit

        `when`("존재하는 메뉴라면") {
            useCase.changeMenuGroup(menuId) { menuGroup }

            then("메뉴의 상태를 변경 할 수 있다.") {
                coVerify { repository.updateMenuGroupId(any()) }
            }
        }

        `when`("존재하지 않는 메뉴라면") {
            coEvery { repository.findById(any()) } returns null

            then("메뉴의 상태를 변경 할 수 없다.") {
                val notExistId = 0L
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeMenuGroup(notExistId) { menuGroup }
                }
                ex.message shouldBe "${notExistId}번 메뉴를 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 메뉴그룹이면") {
            coEvery { menuGroupUseCase.getMenuGroup(any()) } throws NoSuchElementException("0번 메뉴그룹을 찾을 수 없습니다.")

            then("메뉴그룹을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.changeMenuGroup(menuId) { menuGroupUseCase.getMenuGroup(0) }
                }
                ex.message shouldBe "0번 메뉴그룹을 찾을 수 없습니다."
            }
        }
    }
})
