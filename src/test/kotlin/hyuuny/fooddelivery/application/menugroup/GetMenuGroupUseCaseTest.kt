package hyuuny.fooddelivery.application.menugroup

import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetMenuGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuGroupRepository>()
    val useCase = MenuGroupUseCase(repository)

    given("메뉴그룹을 상세조회 할 때") {
        val now = LocalDateTime.now()
        val expectedMenuGroup = MenuGroup(
            id = 1,
            storeId = 1L,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.findById(any()) } returns expectedMenuGroup

        `when`("존재하는 아이디이면") {
            val result = useCase.getMenuGroup(expectedMenuGroup.id!!)

            then("메뉴그룹을 상세조회 할 수 있다.") {
                result.id shouldBe expectedMenuGroup.id
                result.storeId shouldBe expectedMenuGroup.storeId
                result.name shouldBe expectedMenuGroup.name
                result.priority shouldBe expectedMenuGroup.priority
                result.description shouldBe expectedMenuGroup.description
                result.createdAt shouldBe expectedMenuGroup.createdAt
                result.updatedAt shouldBe expectedMenuGroup.updatedAt
            }
        }

        `when`("존재하지 않는 아이디이면") {
            coEvery { repository.findById(any()) } returns null

            then("메뉴그룹을 상세조회 할 수 없다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getMenuGroup(0)
                }
                ex.message shouldBe "0번 메뉴그룹을 찾을 수 없습니다."
            }
        }
    }
})
