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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

        coEvery { useCase.createReview(any(), any(), any(), any()) } returns review
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

    @DisplayName("매장에 등록된 리뷰내역을 볼 수 있다.")
    @Test
    fun getReviewsByStore() {
        val ids = listOf(1L, 2L, 3L, 4L)
        val userIds = listOf(84L, 91L, 13L, 49L)
        val storeId = 52L
        val orderIds = listOf(1938L, 2831L, 2901L, 3899L)

        val now = LocalDateTime.now()
        val users = userIds.map {
            User(
                id = it,
                name = "${it}번 회원",
                nickname = "회원 $it",
                email = "shyune${it}@knou.ac.kr",
                phoneNumber = "010-1234-12${it}",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
                createdAt = now,
                updatedAt = now,
            )
        }

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

        val randomScores = ids.map { (Math.random() * 5 + 1).toInt() }
        val reviews = ids.mapIndexed { idx, it ->
            Review(
                id = it,
                userId = userIds[idx],
                storeId = storeId,
                orderId = orderIds[idx],
                score = randomScores[idx],
                content = "맛있어요. 다음에 또 주문할게요.",
                createdAt = now,
            )
        }
        val reviewPhotos = ids.mapIndexed { idx, it ->
            ReviewPhoto(
                id = idx.toLong() + 1,
                reviewId = it,
                photoUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-1.jpg",
                createdAt = now
            )
        }

        val sortedReviews = reviews.sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(sortedReviews, pageable, reviews.size.toLong())
        coEvery { useCase.getReviewByApiCondition(any(), any()) } returns page
        coEvery { reviewPhotoUseCase.getAllByReviewIdIn(any()) } returns reviewPhotos
        coEvery { userUseCase.getAllByIds(any()) } returns users
        coEvery { storeUseCase.getAllByIds(any()) } returns listOf(store)
        coEvery { useCase.getAllByUserIds(any()) } returns sortedReviews

        webTestClient.get().uri("/api/v1/reviews?storeId=${storeId}&sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content").isArray
            .jsonPath("$.content.length()").isEqualTo(reviews.size)
            .jsonPath("$.content[0].id").isEqualTo(reviews[3].id!!)
            .jsonPath("$.content[0].userId").isEqualTo(reviews[3].userId)
            .jsonPath("$.content[0].storeId").isEqualTo(reviews[3].storeId)
            .jsonPath("$.content[0].orderId").isEqualTo(reviews[3].orderId)
            .jsonPath("$.content[0].storeName").isEqualTo(store.name)
            .jsonPath("$.content[0].userNickname").isEqualTo(users[3].nickname)
            .jsonPath("$.content[0].userImageUrl").isEqualTo(users[3].imageUrl!!)
            .jsonPath("$.content[0].score").isEqualTo(randomScores[3])
            .jsonPath("$.content[0].averageScore").isEqualTo(randomScores[3] / 1)
            .jsonPath("$.content[0].reviewCount").isEqualTo(1)
            .jsonPath("$.content[0].content").isEqualTo(reviews[3].content)
            .jsonPath("$.content[0].photos[0].reviewId").isEqualTo(reviewPhotos[3].reviewId)
            .jsonPath("$.content[0].photos[0].photoUrl").isEqualTo(reviewPhotos[3].photoUrl)

            .jsonPath("$.content[1].id").isEqualTo(reviews[2].id!!)
            .jsonPath("$.content[1].userId").isEqualTo(reviews[2].userId)
            .jsonPath("$.content[1].storeId").isEqualTo(reviews[2].storeId)
            .jsonPath("$.content[1].orderId").isEqualTo(reviews[2].orderId)
            .jsonPath("$.content[1].storeName").isEqualTo(store.name)
            .jsonPath("$.content[1].userNickname").isEqualTo(users[2].nickname)
            .jsonPath("$.content[1].userImageUrl").isEqualTo(users[2].imageUrl!!)
            .jsonPath("$.content[1].score").isEqualTo(randomScores[2])
            .jsonPath("$.content[1].averageScore").isEqualTo(randomScores[2] / 1)
            .jsonPath("$.content[1].reviewCount").isEqualTo(1)
            .jsonPath("$.content[1].content").isEqualTo(reviews[2].content)
            .jsonPath("$.content[1].photos[0].reviewId").isEqualTo(reviewPhotos[2].reviewId)
            .jsonPath("$.content[1].photos[0].photoUrl").isEqualTo(reviewPhotos[2].photoUrl)

            .jsonPath("$.content[2].id").isEqualTo(reviews[1].id!!)
            .jsonPath("$.content[2].userId").isEqualTo(reviews[1].userId)
            .jsonPath("$.content[2].storeId").isEqualTo(reviews[1].storeId)
            .jsonPath("$.content[2].orderId").isEqualTo(reviews[1].orderId)
            .jsonPath("$.content[2].storeName").isEqualTo(store.name)
            .jsonPath("$.content[2].userNickname").isEqualTo(users[1].nickname)
            .jsonPath("$.content[2].userImageUrl").isEqualTo(users[1].imageUrl!!)
            .jsonPath("$.content[2].score").isEqualTo(randomScores[1])
            .jsonPath("$.content[2].averageScore").isEqualTo(randomScores[1] / 1)
            .jsonPath("$.content[2].reviewCount").isEqualTo(1)
            .jsonPath("$.content[2].content").isEqualTo(reviews[1].content)
            .jsonPath("$.content[2].photos[0].reviewId").isEqualTo(reviewPhotos[1].reviewId)
            .jsonPath("$.content[2].photos[0].photoUrl").isEqualTo(reviewPhotos[1].photoUrl)

            .jsonPath("$.content[3].id").isEqualTo(reviews[0].id!!)
            .jsonPath("$.content[3].userId").isEqualTo(reviews[0].userId)
            .jsonPath("$.content[3].storeId").isEqualTo(reviews[0].storeId)
            .jsonPath("$.content[3].orderId").isEqualTo(reviews[0].orderId)
            .jsonPath("$.content[3].storeName").isEqualTo(store.name)
            .jsonPath("$.content[3].userNickname").isEqualTo(users[0].nickname)
            .jsonPath("$.content[3].userImageUrl").isEqualTo(users[0].imageUrl!!)
            .jsonPath("$.content[3].score").isEqualTo(randomScores[0])
            .jsonPath("$.content[3].averageScore").isEqualTo(randomScores[0] / 1)
            .jsonPath("$.content[3].reviewCount").isEqualTo(1)
            .jsonPath("$.content[3].content").isEqualTo(reviews[0].content)
            .jsonPath("$.content[3].photos[0].reviewId").isEqualTo(reviewPhotos[0].reviewId)
            .jsonPath("$.content[3].photos[0].photoUrl").isEqualTo(reviewPhotos[0].photoUrl)

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
    }

    @DisplayName("회원은 자신의 리뷰 등록 내역을 볼 수 있다.")
    @Test
    fun getReviewsByUserId() {
        val ids = listOf(1L, 2L, 3L, 4L)
        val userId = 84L
        val storeIds = listOf(52L, 592L, 690L, 782L)
        val orderIds = listOf(1938L, 2831L, 2901L, 3899L)

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

        val stores = storeIds.mapIndexed { idx, it ->
            Store(
                id = it,
                categoryId = 2L,
                deliveryType = DeliveryType.OUTSOURCING,
                name = "${it}번가 피자",
                ownerName = "나피자",
                taxId = "125-21-3892${idx}",
                deliveryFee = 0,
                minimumOrderAmount = 14000,
                iconImageUrl = "icon-image-url-1.jpg",
                description = "안녕하세요!",
                foodOrigin = "슈퍼빽보이'카나디언:돼지고기(국내산), 페퍼로니: 돼지고기(국내산과 외국산 섞음), 소고기(호주산), 베이컨:돼지고기(미국산)",
                phoneNumber = "02-1231-2308",
                createdAt = now,
                updatedAt = now,
            )
        }

        val randomScores = ids.map { (Math.random() * 5 + 1).toInt() }
        val reviews = ids.mapIndexed { idx, it ->
            Review(
                id = it,
                userId = userId,
                storeId = storeIds[idx],
                orderId = orderIds[idx],
                score = randomScores[idx],
                content = "맛있어요. 다음에 또 주문할게요.",
                createdAt = now,
            )
        }
        val reviewPhotos = ids.mapIndexed { idx, it ->
            ReviewPhoto(
                id = idx.toLong() + 1,
                reviewId = it,
                photoUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-1.jpg",
                createdAt = now
            )
        }

        val sortedReviews = reviews.sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(sortedReviews, pageable, reviews.size.toLong())
        coEvery { useCase.getReviewByApiCondition(any(), any()) } returns page
        coEvery { reviewPhotoUseCase.getAllByReviewIdIn(any()) } returns reviewPhotos
        coEvery { userUseCase.getAllByIds(any()) } returns listOf(user)
        coEvery { storeUseCase.getAllByIds(any()) } returns stores
        coEvery { useCase.getAllByUserIds(any()) } returns sortedReviews

        val expectedAverageScore = randomScores.sumOf { it } / reviews.size
        webTestClient.get().uri("/api/v1/reviews?userId=${userId}&sort=id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content").isArray
            .jsonPath("$.content.length()").isEqualTo(reviews.size)
            .jsonPath("$.content[0].id").isEqualTo(reviews[3].id!!)
            .jsonPath("$.content[0].userId").isEqualTo(reviews[3].userId)
            .jsonPath("$.content[0].storeId").isEqualTo(reviews[3].storeId)
            .jsonPath("$.content[0].orderId").isEqualTo(reviews[3].orderId)
            .jsonPath("$.content[0].storeName").isEqualTo(stores[3].name)
            .jsonPath("$.content[0].userNickname").isEqualTo(user.nickname)
            .jsonPath("$.content[0].userImageUrl").isEqualTo(user.imageUrl!!)
            .jsonPath("$.content[0].score").isEqualTo(randomScores[3])
            .jsonPath("$.content[0].averageScore").isEqualTo(expectedAverageScore)
            .jsonPath("$.content[0].reviewCount").isEqualTo(4)
            .jsonPath("$.content[0].content").isEqualTo(reviews[3].content)
            .jsonPath("$.content[0].photos[0].reviewId").isEqualTo(reviewPhotos[3].reviewId)
            .jsonPath("$.content[0].photos[0].photoUrl").isEqualTo(reviewPhotos[3].photoUrl)

            .jsonPath("$.content[1].id").isEqualTo(reviews[2].id!!)
            .jsonPath("$.content[1].userId").isEqualTo(reviews[2].userId)
            .jsonPath("$.content[1].storeId").isEqualTo(reviews[2].storeId)
            .jsonPath("$.content[1].orderId").isEqualTo(reviews[2].orderId)
            .jsonPath("$.content[1].storeName").isEqualTo(stores[2].name)
            .jsonPath("$.content[1].userNickname").isEqualTo(user.nickname)
            .jsonPath("$.content[1].userImageUrl").isEqualTo(user.imageUrl!!)
            .jsonPath("$.content[1].score").isEqualTo(randomScores[2])
            .jsonPath("$.content[1].averageScore").isEqualTo(expectedAverageScore)
            .jsonPath("$.content[1].reviewCount").isEqualTo(4)
            .jsonPath("$.content[1].content").isEqualTo(reviews[2].content)
            .jsonPath("$.content[1].photos[0].reviewId").isEqualTo(reviewPhotos[2].reviewId)
            .jsonPath("$.content[1].photos[0].photoUrl").isEqualTo(reviewPhotos[2].photoUrl)

            .jsonPath("$.content[2].id").isEqualTo(reviews[1].id!!)
            .jsonPath("$.content[2].userId").isEqualTo(reviews[1].userId)
            .jsonPath("$.content[2].storeId").isEqualTo(reviews[1].storeId)
            .jsonPath("$.content[2].orderId").isEqualTo(reviews[1].orderId)
            .jsonPath("$.content[2].storeName").isEqualTo(stores[1].name)
            .jsonPath("$.content[2].userNickname").isEqualTo(user.nickname)
            .jsonPath("$.content[2].userImageUrl").isEqualTo(user.imageUrl!!)
            .jsonPath("$.content[2].score").isEqualTo(randomScores[1])
            .jsonPath("$.content[2].averageScore").isEqualTo(expectedAverageScore)
            .jsonPath("$.content[2].reviewCount").isEqualTo(4)
            .jsonPath("$.content[2].content").isEqualTo(reviews[1].content)
            .jsonPath("$.content[2].photos[0].reviewId").isEqualTo(reviewPhotos[1].reviewId)
            .jsonPath("$.content[2].photos[0].photoUrl").isEqualTo(reviewPhotos[1].photoUrl)

            .jsonPath("$.content[3].id").isEqualTo(reviews[0].id!!)
            .jsonPath("$.content[3].userId").isEqualTo(reviews[0].userId)
            .jsonPath("$.content[3].storeId").isEqualTo(reviews[0].storeId)
            .jsonPath("$.content[3].orderId").isEqualTo(reviews[0].orderId)
            .jsonPath("$.content[3].storeName").isEqualTo(stores[0].name)
            .jsonPath("$.content[3].userNickname").isEqualTo(user.nickname)
            .jsonPath("$.content[3].userImageUrl").isEqualTo(user.imageUrl!!)
            .jsonPath("$.content[3].score").isEqualTo(randomScores[0])
            .jsonPath("$.content[3].averageScore").isEqualTo(expectedAverageScore)
            .jsonPath("$.content[3].reviewCount").isEqualTo(4)
            .jsonPath("$.content[3].content").isEqualTo(reviews[0].content)
            .jsonPath("$.content[3].photos[0].reviewId").isEqualTo(reviewPhotos[0].reviewId)
            .jsonPath("$.content[3].photos[0].photoUrl").isEqualTo(reviewPhotos[0].photoUrl)

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
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
