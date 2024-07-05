package hyuuny.fooddelivery.application.menu

import CreateMenuRequest
import hyuuny.fooddelivery.application.menugroup.MenuGroupUseCase
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.infrastructure.menu.MenuRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

internal class CreateMenuUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuRepository>()
    val useCase = MenuUseCase(repository)
    val menuGroupUseCase = mockk<MenuGroupUseCase>()

    given("메뉴를 등록하면서") {
        val menuGroupId = 1L

        val now = LocalDateTime.now()
        val menuGroup = MenuGroup(
            id = menuGroupId,
            storeId = 1L,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now
        )

        val request = CreateMenuRequest(
            menuGroupId = menuGroupId,
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살"
        )
        val expectedMenu = Menu(
            id = 1,
            menuGroupId = menuGroupId,
            name = "싸이버거",
            price = 6000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "cyburger-image-url",
            description = "[베스트]닭다리살",
            createdAt = now,
            updatedAt = now
        )
        coEvery { menuGroupUseCase.getMenuGroup(any()) } returns menuGroup
        coEvery { repository.insert(any()) } returns expectedMenu

        `when`("금액을 0이상으로 입력하면") {
            val result = useCase.createMenu(request) { menuGroup }

            then("메뉴를 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.menuGroupId shouldBe request.menuGroupId
                result.name shouldBe request.name
                result.price shouldBe request.price
                result.status shouldBe request.status
                result.popularity shouldBe request.popularity
                result.imageUrl shouldBe request.imageUrl
                result.description shouldBe request.description
                result.createdAt.shouldNotBeNull()
                result.updatedAt.shouldNotBeNull()
            }
        }

        `when`("금액을 0원 이하로 입력하면") {
            then("메뉴를 등록할 수 없다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createMenu(
                        CreateMenuRequest(
                            menuGroupId = 1L,
                            name = "싸이버거",
                            price = 0,
                            status = MenuStatus.ON_SALE,
                            popularity = true,
                            imageUrl = "cyburger-image-url",
                            description = "[베스트]닭다리살"
                        )
                    ) { menuGroup }
                }
                ex.message shouldBe "금액은 0이상이여야 합니다."
            }
        }

        `when`("존재하지 않는 메뉴그룹이면") {
            coEvery { menuGroupUseCase.getMenuGroup(any()) } throws NoSuchElementException("0번 메뉴그룹을 찾을 수 없습니다.")

            then("메뉴그룹을 찾을 수 업다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createMenu(request) { menuGroupUseCase.getMenuGroup(0) }
                }
                ex.message shouldBe "0번 메뉴그룹을 찾을 수 없습니다."
            }
        }
    }
})