package hyuuny.fooddelivery.application.menugroup

import CreateMenuGroupRequest
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.menugroup.MenuGroup
import hyuuny.fooddelivery.domain.store.Store
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
    val storeUseCase = mockk<StoreUseCase>()

    Given("메뉴 그룹을 등록하면서") {
        val storeId = 1L

        val now = LocalDateTime.now()
        val store = Store(
            id = storeId,
            categoryId = 2L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "백종원의 빽보이피자",
            ownerName = "나피자",
            taxId = "125-21-38923",
            deliveryFee = 0,
            minimumOrderAmount = 14000,
            iconImageUrl = "icon-image-url-1.jpg",
            description = "안녕하세요. 백종원이 빽보이피자입니다 :)\n" +
                    " ★ 음료는 기본 제공되지 않습니다. 필요하신분은 추가 주문 부탁드립니다.\n" +
                    " ★ 다양한 리뷰이베트는 리뷰칸을 확인해주세요!",
            foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
            phoneNumber = "02-1231-2308",
            createdAt = now.plusHours(1),
            updatedAt = now.plusHours(1),
        )

        val request = CreateMenuGroupRequest(
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
        )
        val expectedMenuGroup = MenuGroup(
            id = 1,
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { repository.insert(any()) } returns expectedMenuGroup

        `when`("이름을 2글자 이상으로 입력하면") {
            val result = useCase.createMenuGroup(request) { storeUseCase.getStore(storeId) }

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
                        CreateMenuGroupRequest(storeId, "짱", 1, "자신있게 추천드려요!")
                    ) { storeUseCase.getStore(storeId) }
                }
                ex.message shouldBe "이름은 2자 이상이어야 합니다."
            }
        }

        `when`("존재하지 않는 매장이면") {
            coEvery { storeUseCase.getStore(any()) } throws NoSuchElementException("0번 매장을 찾을 수 없습니다.")

            then("매장을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createMenuGroup(request) { storeUseCase.getStore(0) }
                }
                ex.message shouldBe "0번 매장을 찾을 수 없습니다."
            }
        }
    }

})
