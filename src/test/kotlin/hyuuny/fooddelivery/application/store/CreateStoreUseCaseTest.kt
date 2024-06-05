package hyuuny.fooddelivery.application.store

import CreateStoreDetailRequest
import CreateStoreImageRequest
import CreateStoreRequest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.infrastructure.store.StoreRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateStoreUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreRepository>()
    val useCase = StoreUseCase(repository)

    Given("매장을 등록하면서") {
        val request = CreateStoreRequest(
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며, BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            storeDetail = CreateStoreDetailRequest(
                zipCode = "12345",
                address = "서울시 강남구 강남대로123길 12",
                detailedAddress = "1층 101호",
                openHours = "매일 오전 11:00 ~ 오후 11시 30분",
                closedDay = null,
            ),
            storeImage = CreateStoreImageRequest(
                imageUrls = listOf(
                    "image-url-1.jpg",
                    "image-url-2.jpg",
                    "image-url-3.jpg",
                )
            )
        )

        val now = LocalDateTime.now()
        val expectedStore = Store(
            id = 1L,
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며, BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { repository.insert(any()) } returns expectedStore

        `when`("입력한 매장 정보로") {
            val result = useCase.createStore(request, now)

            then("매장을 등록할 수 있다.") {
                result.id.shouldNotBeNull()
                result.categoryId shouldBe request.categoryId
                result.deliveryType shouldBe request.deliveryType
                result.name shouldBe request.name
                result.ownerName shouldBe request.ownerName
                result.taxId shouldBe request.taxId
                result.deliveryFee shouldBe request.deliveryFee
                result.minimumOrderAmount shouldBe request.minimumOrderAmount
                result.iconImageUrl shouldBe request.iconImageUrl
                result.description shouldBe request.description
                result.foodOrigin shouldBe request.foodOrigin
                result.phoneNumber shouldBe request.phoneNumber
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }
    }

})