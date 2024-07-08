package hyuuny.fooddelivery.menugroups.application

import ReorderMenuGroupRequest
import ReorderMenuGroupRequests
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.menugroups.domain.MenuGroup
import hyuuny.fooddelivery.menugroups.infrastructure.MenuGroupRepository
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.stores.domain.Store
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class ReOrderMenuGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<MenuGroupRepository>()
    val useCase = MenuGroupUseCase(repository)
    val storeUseCase = mockk<StoreUseCase>()

    given("메뉴그룹 순서를 수정 할 때") {
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

        val firstOptionGroup = MenuGroup(
            id = 1L,
            storeId = storeId,
            name = "추천메뉴",
            priority = 1,
            description = "자신있게 추천드려요!",
            createdAt = now,
            updatedAt = now
        )
        val secondOptionGroup = MenuGroup(
            id = 2L,
            storeId = storeId,
            name = "사장님 추천",
            priority = 2,
            createdAt = now,
            updatedAt = now
        )
        val thirdOptionGroup = MenuGroup(
            id = 3L,
            storeId = storeId,
            name = "사이드 메뉴",
            priority = 3,
            description = "여러가지 사이드 메뉴",
            createdAt = now,
            updatedAt = now
        )
        val menuGroups = listOf(firstOptionGroup, secondOptionGroup, thirdOptionGroup)

        val requests = ReorderMenuGroupRequests(
            storeId = storeId,
            reOrderedMenuGroups = listOf(
                ReorderMenuGroupRequest(3, 1),
                ReorderMenuGroupRequest(1, 2),
                ReorderMenuGroupRequest(2, 3),
            )
        )
        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { repository.findAllByStoreId(any()) } returns menuGroups
        coEvery { repository.bulkUpdatePriority(any()) } returns Unit

        `when`("기존 메뉴그룹과 개수가 일치하면") {
            useCase.reOrderMenuGroups(requests) { store }

            then("메뉴그룹의 순서를 수정 할 수 있다.") {
                coVerify { repository.bulkUpdatePriority(any()) }
            }
        }

        `when`("기존 메뉴그룹과 개수가 일치하지 않으면") {
            val incorrectRequests = ReorderMenuGroupRequests(
                storeId = storeId,
                reOrderedMenuGroups = listOf(
                    ReorderMenuGroupRequest(3, 1),
                    ReorderMenuGroupRequest(1, 2)
                )
            )
            coEvery { repository.findAllByStoreId(any()) } returns menuGroups

            then("메뉴그룹의 순서를 수정할 수 없다.") {
                val ex = shouldThrow<IllegalStateException> {
                    useCase.reOrderMenuGroups(incorrectRequests) { store }
                }
                ex.message shouldBe "메뉴그룹의 개수가 일치하지 않습니다."
            }
        }

        `when`("존재하지 않는 매장이면") {
            coEvery { storeUseCase.getStore(any()) } throws NoSuchElementException("0번 매장을 찾을 수 없습니다.")

            then("매장을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.reOrderMenuGroups(requests) { storeUseCase.getStore(0) }
                }
                ex.message shouldBe "0번 매장을 찾을 수 없습니다."
            }
        }
    }
})
