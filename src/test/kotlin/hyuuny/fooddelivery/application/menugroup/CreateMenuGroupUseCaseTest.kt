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

class CreateMenuGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuGroupRepository>()
    val useCase = MenuGroupUseCase(repository)

    Given("메뉴 그룹을 등록하면서") {
        val storeId = 1L
        val request = CreateMenuGroupRequest(
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
        )

        val now = LocalDateTime.now()
        val expectedMenuGroup = MenuGroup(
            id = 1,
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.insert(any()) } returns expectedMenuGroup

        `when`("이름을 2글자 이상으로 입력하면") {
            val result = useCase.createMenuGroup(request)

            then("메뉴 그룹을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.storeId shouldBe request.storeId
                result.name shouldBe request.name
                result.priority shouldBe request.priority
                result.description shouldBe request.description
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("이름을 2글자 이하로 입력하면") {
            then("메뉴 그룹을 등록할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createMenuGroup(
                        CreateMenuGroupRequest(
                            storeId = storeId,
                            name = "짱",
                            priority = 1,
                            description = "자신있게 추천드려요!",
                        )
                    )
                }
                ex.message shouldBe "이름은 2자 이상이어야 합니다."
            }
        }
    }

})
