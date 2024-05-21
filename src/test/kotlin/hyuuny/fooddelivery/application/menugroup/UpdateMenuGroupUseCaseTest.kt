package hyuuny.fooddelivery.application.menugroup

import UpdateMenuGroupRequest
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class UpdateMenuGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuGroupRepository>()
    val useCase = MenuGroupUseCase(repository)

    given("메뉴를 수정 할 때") {
        val menuId = 5L
        val menuGroupId = 1L
        val now = LocalDateTime.now()
        val expectedMenuGroup = MenuGroup(
            id = menuGroupId,
            menuId = menuId,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val request = UpdateMenuGroupRequest(
            name = "사장님 추천 메뉴!",
            menuId = menuId,
            required = false,
        )
        coEvery { repository.findById(any()) } returns expectedMenuGroup
        coEvery { repository.update(any()) } returns Unit

        `when`("메뉴 이름을 2글자 이상으로 입력하면") {
            useCase.updateMenuGroup(menuGroupId, request)
            then("메뉴를 수정 할 수 있다.") {
                coVerify { repository.update(any()) }
            }
        }

        `when`("메뉴 이름을 2글자 미만으로 입력하면") {
            coEvery { repository.findById(any()) } returns expectedMenuGroup

            then("메뉴를 수정 할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.updateMenuGroup(
                        menuGroupId,
                        UpdateMenuGroupRequest(
                            name = "굳",
                            menuId = menuId,
                            required = false,
                        )
                    )
                }
                ex.message shouldBe "이름은 2자 이상이어야 합니다."
            }
        }
    }
})