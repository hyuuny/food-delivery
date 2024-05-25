package hyuuny.fooddelivery.application.optiongroup

import CreateOptionGroupRequest
import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import hyuuny.fooddelivery.infrastructure.optiongroup.OptionGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateOptionGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionGroupRepository>()
    val useCase = OptionGroupUseCase(repository)

    Given("옵션그룹을 등록하면서") {
        val request = CreateOptionGroupRequest(
            menuId = 1L,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
        )

        val now = LocalDateTime.now()
        val expectedOptionGroup = OptionGroup(
            id = 1,
            menuId = 1L,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.insert(any()) } returns expectedOptionGroup

        `when`("이름을 2글자 이상으로 입력하면") {
            val result = useCase.createOptionGroup(request)

            then("옵션그룹을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.menuId shouldBe request.menuId
                result.name shouldBe request.name
                result.required shouldBe request.required
                result.priority shouldBe request.priority
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("이름을 2글자 이하로 입력하면") {
            then("옵션그룹을 등록할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createOptionGroup(CreateOptionGroupRequest(menuId = 1L, name = "일", required = true, priority = 1))
                }
                ex.message shouldBe "이름은 2자 이상이어야 합니다."
            }
        }
    }

})