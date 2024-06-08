package hyuuny.fooddelivery.application.store

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.infrastructure.store.StoreDetailRepository
import hyuuny.fooddelivery.infrastructure.store.StoreImageRepository
import hyuuny.fooddelivery.infrastructure.store.StoreRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetStoreUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreRepository>()
    val detailRepository = mockk<StoreDetailRepository>()
    val imageRepository = mockk<StoreImageRepository>()
    val useCase = StoreUseCase(repository, detailRepository, imageRepository)

    Given("매장을 상세조회 할 때") {
        val expectedStoreId = 1L
        val now = LocalDateTime.now()
        val expectedStore = Store(
            id = expectedStoreId,
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
        coEvery { repository.findById(any()) } returns expectedStore

        `when`("존재하는 아이디이면") {
            val result = useCase.getStore(expectedStoreId)

            then("매장을 상세조회 할 수 있다.") {
                result.id shouldBe expectedStoreId
                result.categoryId shouldBe expectedStore.categoryId
                result.deliveryType shouldBe expectedStore.deliveryType
                result.name shouldBe expectedStore.name
                result.ownerName shouldBe expectedStore.ownerName
                result.taxId shouldBe expectedStore.taxId
                result.deliveryFee shouldBe expectedStore.deliveryFee
                result.minimumOrderAmount shouldBe expectedStore.minimumOrderAmount
                result.iconImageUrl shouldBe expectedStore.iconImageUrl
                result.description shouldBe expectedStore.description
                result.foodOrigin shouldBe expectedStore.foodOrigin
                result.phoneNumber shouldBe expectedStore.phoneNumber
                result.createdAt.shouldNotBeNull()
                result.updatedAt shouldBe result.createdAt
            }
        }

        `when`("존재하지 않는 아이디이면") {
            coEvery { repository.findById(any()) } returns null

            then("존재하지 않는 매장이라는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.getStore(0)
                }
                ex.message shouldBe "존재하지 않는 매장입니다."
            }
        }
    }

})