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
        val storeId = 1L
        val now = LocalDateTime.now()
        val firstOptionGroup = MenuGroup(
            id = 1L,
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now
        )
        val secondOptionGroup = MenuGroup(
            id = 2L,
            storeId = storeId,
            name = "사장님 추천",
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val thirdOptionGroup = MenuGroup(
            id = 3L,
            storeId = storeId,
            name = "사이드 메뉴",
            priority = 3,
            description = "여러가지 사이드 메뉴",
            createdAt = now,
            updatedAt = now
        )
        val menuGroups = listOf(firstOptionGroup, secondOptionGroup, thirdOptionGroup)

        val requests = ReorderMenuGroupRequests(
            reOrderedMenuGroups = listOf(
                ReorderMenuGroupRequest(3, 1),
                ReorderMenuGroupRequest(1, 2),
                ReorderMenuGroupRequest(2, 3),
            )
        )
        coEvery { repository.findAllByStoreId(any()) } returns menuGroups
        coEvery { repository.bulkUpdatePriority(any()) } returns Unit

        `when`("기존 메뉴그룹과 개수가 일치하면") {
            useCase.reOrderMenuGroups(storeId, requests)

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
            coEvery { repository.findAllByStoreId(any()) } returns menuGroups

            then("메뉴그룹의 순서를 수정할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.reOrderMenuGroups(storeId, incorrectRequests)
                }
                ex.message shouldBe "메뉴그룹의 개수가 일치하지 않습니다."
            }
        }
    }
})
