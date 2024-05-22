package hyuuny.fooddelivery.application.menugroup

import ReorderMenuGroupRequest
import ReorderMenuGroupRequests
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class ReOrderMenuGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuGroupRepository>()
    val useCase = MenuGroupUseCase(repository)

    given("메뉴그룹 순서를 수정 할 때") {
        val menuId = 1L
        val now = LocalDateTime.now()
        val firstMenuGroup = MenuGroup(
            id = 1L,
            menuId = menuId,
            name = "치킨세트",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val secondMenuGroup = MenuGroup(
            id = 2L,
            menuId = menuId,
            name = "사장님 추천",
            required = true,
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val thirdMenuGroup = MenuGroup(
            id = 3L,
            menuId = menuId,
            name = "사이드!",
            required = true,
            priority = 3,
            createdAt = now,
            updatedAt = now
        )
        val menuGroups = listOf(firstMenuGroup, secondMenuGroup, thirdMenuGroup)

        val requests = ReorderMenuGroupRequests(
            reOrderedMenuGroups = listOf(
                ReorderMenuGroupRequest(3, 1),
                ReorderMenuGroupRequest(1, 2),
                ReorderMenuGroupRequest(2, 3),
            )
        )
        coEvery { repository.findAllByMenuId(any()) } returns menuGroups
        coEvery { repository.bulkUpdatePriority(any()) } returns Unit

        `when`("기존 메뉴그룹과 개수가 일치하면") {
            useCase.reOrderMenuGroups(menuId, requests)

            then("메뉴그룹의 순서를 수정 할 수 있다.") {
                coVerify { repository.bulkUpdatePriority(any()) }
            }
        }

        `when`("기존 메뉴그룹과 개수가 일치하지 않으면") {
            val incorrectRequests = ReorderMenuGroupRequests(
                reOrderedMenuGroups = listOf(
                    ReorderMenuGroupRequest(3, 1),
                    ReorderMenuGroupRequest(1, 2)
                )
            )
            coEvery { repository.findAllByMenuId(any()) } returns menuGroups

            then("메뉴그룹의 순서를 수정할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.reOrderMenuGroups(menuId, incorrectRequests)
                }
                ex.message shouldBe "메뉴그룹의 개수가 일치하지 않습니다."
            }
        }
    }
})