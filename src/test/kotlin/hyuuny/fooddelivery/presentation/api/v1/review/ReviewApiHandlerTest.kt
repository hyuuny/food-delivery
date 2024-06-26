package hyuuny.fooddelivery.presentation.api.v1.review

import CreateReviewPhotoRequest
import CreateReviewRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.review.ReviewPhotoUseCase
import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.review.ReviewPhoto
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import hyuuny.fooddelivery.presentation.api.v1.review.response.ReviewResponse
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime

class ReviewApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: ReviewUseCase

    @MockkBean
    private lateinit var reviewPhotoUseCase: ReviewPhotoUseCase

    @MockkBean
    private lateinit var userUseCase: UserUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

    @DisplayName("회원은 주문에 리뷰를 작성할 수 있다.")
    @Test
    fun createReview() {
        val id = 1L
        val userId = 84L
        val storeId = 52L
        val orderId = 1938L

        val now = LocalDateTime.now()
        val user = User(
            id = userId,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )

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

        val request = CreateReviewRequest(
            storeId = storeId,
            orderId = orderId,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            photos = listOf(
                CreateReviewPhotoRequest("https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-1.jpg"),
                CreateReviewPhotoRequest("https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-2.jpg"),
                CreateReviewPhotoRequest("https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-3.jpg"),
            )
        )

        val review = generateReview(id, userId, request, now)
        val reviewPhotos = generateReviewPhotos(id, request.photos, now)

        coEvery { useCase.createReview(any(),any(), any(), any()) } returns review
        coEvery { reviewPhotoUseCase.getAllByReviewId(any()) } returns reviewPhotos
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { storeUseCase.getStore(any()) } returns store

        val expectedResponse = ReviewResponse.from(review, user, store, reviewPhotos)

        webTestClient.post().uri("/api/v1/users/$userId/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.userNickname").isEqualTo(expectedResponse.userNickname)
            .jsonPath("$.userImageUrl").isEqualTo(expectedResponse.userImageUrl!!)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId)
            .jsonPath("$.storeName").isEqualTo(expectedResponse.storeName)
            .jsonPath("$.orderId").isEqualTo(expectedResponse.orderId)
            .jsonPath("$.score").isEqualTo(expectedResponse.score)
            .jsonPath("$.content").isEqualTo(expectedResponse.content)
            .jsonPath("$.createdAt").exists()
    }

    private fun generateReview(id: Long, userId: Long, request: CreateReviewRequest, now: LocalDateTime): Review {
        return Review(
            id = id,
            userId = userId,
            storeId = request.storeId,
            orderId = request.orderId,
            score = request.score,
            content = request.content,
            createdAt = now,
        )
    }

    private fun generateReviewPhotos(
        reviewId: Long,
        requests: List<CreateReviewPhotoRequest>,
        now: LocalDateTime
    ): List<ReviewPhoto> {
        return requests.mapIndexed { idx, it ->
            ReviewPhoto(
                id = idx.toLong() + 1,
                reviewId = reviewId,
                photoUrl = it.photoUrl,
                createdAt = now,
            )
        }
    }


}
