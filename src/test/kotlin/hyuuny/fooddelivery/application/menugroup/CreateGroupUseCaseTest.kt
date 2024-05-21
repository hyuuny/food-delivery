package hyuuny.fooddelivery.application.menugroup

import CreateMenuGroupRequest
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menugroup.MenuGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuGroupRepository>()
    val useCase = MenuGroupUseCase(repository)

    Given("메뉴 그룹을 등록하면서") {
        val request = CreateMenuGroupRequest(
            menuId = 1L,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
        )

        val now = LocalDateTime.now()
        val expectedMenuGroup = MenuGroup(
            id = 1,
            menuId = 1L,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.insert(any()) } returns expectedMenuGroup

        `when`("메뉴 이름을 2글자 이상으로 입력하면") {
            val result = useCase.createMenuGroup(request)

            then("메뉴 그룹을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.menuId shouldBe request.menuId
                result.name shouldBe request.name
                result.required shouldBe request.required
                result.priority shouldBe request.priority
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("메뉴 이름을 2글자 이하로 입력하면") {
            then("메뉴 그룹을 등록할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createMenuGroup(CreateMenuGroupRequest(menuId = 1L, name = "일", required = true, priority = 1))
                }
                ex.message shouldBe "이름은 2자 이상이어야 합니다."
            }
        }
    }

})