package hyuuny.fooddelivery.reviews.application

import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.reviews.infrastructure.ReviewPhotoRepository
import hyuuny.fooddelivery.reviews.infrastructure.ReviewRepository
import hyuuny.fooddelivery.stores.domain.Store
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetAverageStoreReviewUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewRepository>()
    val reviewPhotoRepository = mockk<ReviewPhotoRepository>()
    val useCase = ReviewUseCase(repository, reviewPhotoRepository)

    Given("매장의 리뷰 총 평점을 조회하면") {
        val userId = 1L
        val storeId = 1L
        val orderId = 1L
        val reviewId = 1L

        val now = LocalDateTime.now()
        Store(
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
            createdAt = now.minusYears(1),
            updatedAt = now.minusYears(1),
        )

        val firstReview = Review(
            id = reviewId,
            userId = userId,
            storeId = storeId,
            orderId = orderId,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now,
        )

        val secondReview = Review(
            id = reviewId,
            userId = userId,
            storeId = storeId,
            orderId = orderId,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now,
        )

        val thirdReview = Review(
            id = reviewId,
            userId = userId,
            storeId = storeId,
            orderId = orderId,
            score = 3,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now,
        )

        val fourthReview = Review(
            id = reviewId,
            userId = userId,
            storeId = storeId,
            orderId = orderId,
            score = 2,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now,
        )

        val totalScore = firstReview.score + secondReview.score + thirdReview.score + fourthReview.score
        val expectedAverageScore = totalScore.toDouble() / 4
        coEvery { repository.findAverageScoreByStoreId(any()) } returns mapOf(storeId to expectedAverageScore)

        `when`("매장에 등록된 모든 리뷰의 평점을 계산해서") {
            val result = useCase.getAverageScoreByStoreIds(listOf(storeId))

            then("반환한다.") {
                result shouldBe mapOf(storeId to expectedAverageScore)
            }
        }
    }

})
