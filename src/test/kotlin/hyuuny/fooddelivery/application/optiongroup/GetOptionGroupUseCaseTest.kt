package hyuuny.fooddelivery.application.optiongroup

import hyuuny.fooddelivery.domain.optiongroup.OptionGroup
import hyuuny.fooddelivery.infrastructure.optiongroup.OptionGroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class GetOptionGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<OptionGroupRepository>()
    val useCase = OptionGroupUseCase(repository)

    given("옵션그룹을 상세조회 할 때") {
        val expectedMenuId = 5L
        val expectedOptionGroupId = 1L
        val now = LocalDateTime.now()
        val expectedOptionGroup = OptionGroup(
            id = expectedOptionGroupId,
            menuId = expectedMenuId,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now
        )
        coEvery { repository.findById(any()) } returns expectedOptionGroup

        `when`("존재하는 아이디이면") {
            val result = useCase.getOptionGroup(expectedOptionGroupId)

            then("옵션그룹을를 상세조회 할 수 있다.") {
                result.id shouldBe expectedOptionGroupId
                result.menuId shouldBe expectedMenuId
                result.name shouldBe expectedOptionGroup.name
                result.required shouldBe expectedOptionGroup.required
                result.priority shouldBe expectedOptionGroup.priority
                result.createdAt shouldBe expectedOptionGroup.createdAt
                result.updatedAt shouldBe expectedOptionGroup.updatedAt
            }
        }

        `when`("존재하지 않는 아이디이면") {
            coEvery { repository.findById(any()) } returns null

            then("옵션그룹을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getOptionGroup(0)
                }
                ex.message shouldBe "0번 옵션그룹을 찾을 수 없습니다."
            }
        }
    }
})