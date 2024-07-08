package hyuuny.fooddelivery.optiongroups.application

import CreateOptionGroupRequest
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.menus.application.MenuUseCase
import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.optiongroups.domain.OptionGroup
import hyuuny.fooddelivery.optiongroups.infrastructure.OptionGroupRepository
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
    val menuUseCase = mockk<MenuUseCase>()

    Given("옵션그룹을 등록하면서") {
        val now = LocalDateTime.now()
        val menuId = 130L

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

        val request = CreateOptionGroupRequest(
            menuId = menuId,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
        )
        val expectedOptionGroup = OptionGroup(
            id = 1,
            menuId = menuId,
            name = "반반치킨 선택",
            required = true,
            priority = 1,
            createdAt = now,
            updatedAt = now,
        )
        coEvery { menuUseCase.getMenu(any()) } returns menu
        coEvery { repository.insert(any()) } returns expectedOptionGroup

        `when`("이름을 2글자 이상으로 입력하면") {
            val result = useCase.createOptionGroup(request = request, getMenu = { menu })

            then("옵션그룹을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.menuId shouldBe menu.id
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
                    useCase.createOptionGroup(CreateOptionGroupRequest(menuId, "일", true, 1)) { menu }
                }
                ex.message shouldBe "이름은 2자 이상이어야 합니다."
            }
        }

        `when`("존재하지 않는 메뉴이면") {
            coEvery { menuUseCase.getMenu(any()) } throws NoSuchElementException("0번 메뉴를 찾을 수 없습니다.")

            then("메뉴를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createOptionGroup(request) { menuUseCase.getMenu(0) }
                }
                ex.message shouldBe "0번 메뉴를 찾을 수 없습니다."
            }
        }
    }

})
