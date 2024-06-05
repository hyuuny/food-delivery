package hyuuny.fooddelivery.application.store

import CreateStoreDetailRequest
import CreateStoreImageRequest
import UpdateStoreRequest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.infrastructure.store.StoreRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class UpdateStoreUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreRepository>()
    val useCase = StoreUseCase(repository)

    Given("매장을 수정할 때") {
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
            createdAt = now,
            updatedAt = now,
        )

        val request = UpdateStoreRequest(
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
        coEvery { repository.findById(any()) } returns store
        coEvery { repository.update(any()) } returns Unit

        `when`("입력한 매장 정보로") {
            useCase.updateStore(storeId, request, now)

            then("매장을 수정할 수 있다.") {
                coEvery { repository.update(any()) }
            }
        }

        `when`("존재하지 않는 매장 아이디라면") {
            coEvery { repository.findById(any()) } returns null

            then("존재하지 않는 매장이라는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.updateStore(0, request, now)
                }
                ex.message shouldBe "존재하지 않는 매장입니다."
            }
        }
    }

})