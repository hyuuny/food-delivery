package hyuuny.fooddelivery.application.optiongroup

import ReorderOptionGroupRequest
import ReorderOptionGroupRequests
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import hyuuny.fooddelivery.infrastructure.optiongroup.OptionGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class ReOrderOptionGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionGroupRepository>()
    val useCase = OptionGroupUseCase(repository)

    given("옵션그룹 순서를 수정 할 때") {
        val menuId = 1L
        val now = LocalDateTime.now()
        val firstOptionGroup = OptionGroup(
            id = 1L,
            menuId = menuId,
            name = "치킨세트",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        val secondOptionGroup = OptionGroup(
            id = 2L,
            menuId = menuId,
            name = "사장님 추천",
            required = true,
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val thirdOptionGroup = OptionGroup(
            id = 3L,
            menuId = menuId,
            name = "사이드!",
            required = true,
            priority = 3,
            createdAt = now,
            updatedAt = now
        )
        val optionGroups = listOf(firstOptionGroup, secondOptionGroup, thirdOptionGroup)

        val requests = ReorderOptionGroupRequests(
            reOrderedOptionGroups = listOf(
                ReorderOptionGroupRequest(3, 1),
                ReorderOptionGroupRequest(1, 2),
                ReorderOptionGroupRequest(2, 3),
            )
        )
        coEvery { repository.findAllByMenuId(any()) } returns optionGroups
        coEvery { repository.bulkUpdatePriority(any()) } returns Unit

        `when`("기존 옵션그룹과 개수가 일치하면") {
            useCase.reOrderOptionGroups(menuId, requests)

            then("그룹의 순서를 수정 할 수 있다.") {
                coVerify { repository.bulkUpdatePriority(any()) }
            }
        }

        `when`("기존 옵션그룹과 개수가 일치하지 않으면") {
            val incorrectRequests = ReorderOptionGroupRequests(
                reOrderedOptionGroups = listOf(
                    ReorderOptionGroupRequest(3, 1),
                    ReorderOptionGroupRequest(1, 2)
                )
            )
            coEvery { repository.findAllByMenuId(any()) } returns optionGroups

            then("그룹의 순서를 수정할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.reOrderOptionGroups(menuId, incorrectRequests)
                }
                ex.message shouldBe "옵션그룹의 개수가 일치하지 않습니다."
            }
        }
    }
})