package hyuuny.fooddelivery.optiongroups.application

import ReorderOptionGroupRequest
import ReorderOptionGroupRequests
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.menus.application.MenuUseCase
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import hyuuny.fooddelivery.optiongroups.infrastructure.OptionGroupRepository
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
    val menuUseCase = mockk<MenuUseCase>()

    given("옵션그룹 순서를 수정 할 때") {
        val menuId = 1L
        val now = LocalDateTime.now()
        val menu = Menu(
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
            menuId = menuId,
            reOrderedOptionGroups = listOf(
                ReorderOptionGroupRequest(3, 1),
                ReorderOptionGroupRequest(1, 2),
                ReorderOptionGroupRequest(2, 3),
            )
        )
        coEvery { menuUseCase.getMenu(any()) } returns menu
        coEvery { repository.findAllByMenuId(any()) } returns optionGroups
        coEvery { repository.bulkUpdatePriority(any()) } returns Unit

        `when`("기존 옵션그룹과 개수가 일치하면") {
            useCase.reOrderOptionGroups(requests) { menu }

            then("그룹의 순서를 수정 할 수 있다.") {
                coVerify { repository.bulkUpdatePriority(any()) }
            }
        }

        `when`("기존 옵션그룹과 개수가 일치하지 않으면") {
            val incorrectRequests = ReorderOptionGroupRequests(
                menuId = menuId,
                reOrderedOptionGroups = listOf(
                    ReorderOptionGroupRequest(3, 1),
                    ReorderOptionGroupRequest(1, 2)
                )
            )
            coEvery { repository.findAllByMenuId(any()) } returns optionGroups

            then("그룹의 순서를 수정할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.reOrderOptionGroups(incorrectRequests) { menu }
                }
                ex.message shouldBe "옵션그룹의 개수가 일치하지 않습니다."
            }
        }
    }
})
