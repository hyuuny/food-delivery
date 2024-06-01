package hyuuny.fooddelivery.application.store

import CreateStoreDetailRequest
import hyuuny.fooddelivery.domain.store.StoreDetail
import hyuuny.fooddelivery.infrastructure.store.StoreDetailRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateStoreDetailUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreDetailRepository>()
    val useCase = StoreDetailUseCase(repository)

    Given("매장 상세정보를 등록하면서") {
        val request = CreateStoreDetailRequest(
            zipCode = "12345",
            address = "서울시 강남구 강남대로123길 12",
            detailedAddress = "1층 101호",
            openHours = "매일 오전 11:00 ~ 오후 11시 30분",
            closedDay = null,
        )

        val now = LocalDateTime.now()
        val storeId = 1L
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
        coEvery { repository.insert(any()) } returns expectedStoreDetail

        `when`("입력한 매장 상세정보로") {
            val result = useCase.createStoreDetail(storeId, request, now)

            then("매장 정보를 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.storeId shouldBe storeId
                result.zipCode shouldBe request.zipCode
                result.address shouldBe request.address
                result.detailedAddress shouldBe request.detailedAddress
                result.openHours shouldBe request.openHours
                result.closedDay shouldBe request.closedDay
                result.createdAt.shouldNotBeNull()
            }
        }
    }

})