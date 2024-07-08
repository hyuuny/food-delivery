package hyuuny.fooddelivery.stores.application

import hyuuny.fooddelivery.stores.domain.StoreDetail
import hyuuny.fooddelivery.stores.infrastructure.StoreDetailRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetStoreDetailUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreDetailRepository>()
    val useCase = StoreDetailUseCase(repository)

    Given("매장 정보를 상세조회 할 때") {
        val storeId = 1L
        val now = LocalDateTime.now()
        val expectedStoreDetail = StoreDetail(
            id = 1L,
            storeId = storeId,
            zipCode = "12345",
            address = "서울시 강남구 강남대로123길 12",
            detailedAddress = "1층 101호",
            openHours = "매일 오전 11:00 ~ 오후 11시 30분",
            closedDay = null,
            createdAt = now,
        )
        coEvery { repository.findByStoreId(any()) } returns expectedStoreDetail

        `when`("존재하는 매장 아이디이면") {
            val result = useCase.getStoreDetailByStoreId(storeId)

            then("매장 정보를 상세조회 할 수 있다.") {
                result.id shouldBe storeId
                result.storeId shouldBe expectedStoreDetail.storeId
                result.zipCode shouldBe expectedStoreDetail.zipCode
                result.address shouldBe expectedStoreDetail.address
                result.detailedAddress shouldBe expectedStoreDetail.detailedAddress
                result.openHours shouldBe expectedStoreDetail.openHours
                result.closedDay shouldBe expectedStoreDetail.closedDay
                result.createdAt shouldBe expectedStoreDetail.createdAt
            }
        }

        `when`("존재하지 않는 매장 아이디이면") {
            coEvery { repository.findByStoreId(any()) } returns null

            then("매장 정보를 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getStoreDetailByStoreId(0)
                }
                ex.message shouldBe "0번 매장 정보를 찾을 수 없습니다."
            }
        }
    }

})
