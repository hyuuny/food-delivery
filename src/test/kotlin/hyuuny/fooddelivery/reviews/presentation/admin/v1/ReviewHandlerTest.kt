package hyuuny.fooddelivery.reviews.presentation.admin.v1

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.orders.application.OrderItemUseCase
import hyuuny.fooddelivery.orders.domain.OrderItem
import hyuuny.fooddelivery.reviews.application.ReviewPhotoUseCase
import hyuuny.fooddelivery.reviews.application.ReviewUseCase
import hyuuny.fooddelivery.reviews.domain.Review
import hyuuny.fooddelivery.reviews.domain.ReviewPhoto
import hyuuny.fooddelivery.reviews.presentation.admin.v1.response.ReviewResponse
import hyuuny.fooddelivery.stores.application.StoreUseCase
import hyuuny.fooddelivery.stores.domain.Store
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class ReviewHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: ReviewUseCase

    @MockkBean
    private lateinit var reviewPhotoUseCase: ReviewPhotoUseCase

    @MockkBean
    private lateinit var userUseCase: UserUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

    @MockkBean
    private lateinit var orderItemUseCase: OrderItemUseCase

    @DisplayName("관리자는 회원들이 작성한 리뷰 목록을 조회할 수 있다.")
    @Test
    fun getReviews() {
        val ids = listOf(1L, 2L, 3L, 4L)
        val userIds = listOf(84L, 91L, 13L, 49L)
        val storeIds = listOf(52L, 592L, 690L, 782L)
        val orderIds = listOf(1938L, 2831L, 2901L, 3899L)

        val now = LocalDateTime.now()
        val users = userIds.mapIndexed { idx, it ->
            User(
                id = it,
                name = "김성현",
                nickname = "hyuuny",
                email = "shyune@knou.ac.kr",
                phoneNumber = "010-1234-12${it}",
                imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
                createdAt = now,
                updatedAt = now,
            )
        }

        val stores = storeIds.mapIndexed { idx, it ->
            Store(
                id = it,
                categoryId = (Math.random() * 500 + 1).toLong(),
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
        val reviews = users.mapIndexed { idx, it ->
            Review(
                id = ids[idx],
                userId = it.id!!,
                storeId = storeIds[idx],
                orderId = orderIds[idx],
                score = randomScores[idx],
                content = "맛있어요. 다음에 또 주문할게요.",
                createdAt = now,
            )
        }

        val sortedReviews = reviews.sortedByDescending { it.id }
        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(sortedReviews, pageable, reviews.size.toLong())
        coEvery { useCase.getReviewByAdminCondition(any(), any()) } returns page
        coEvery { userUseCase.getAllByIds(any()) } returns users
        coEvery { storeUseCase.getAllByIds(any()) } returns stores

        webTestClient.get().uri("/admin/v1/reviews?sort=id:desc")
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
            .jsonPath("$.content[0].userName").isEqualTo(users[3].name)
            .jsonPath("$.content[0].userNickname").isEqualTo(users[3].nickname)
            .jsonPath("$.content[0].userPhoneNumber").isEqualTo(users[3].phoneNumber)
            .jsonPath("$.content[0].score").isEqualTo(reviews[3].score)

            .jsonPath("$.content[1].id").isEqualTo(reviews[2].id!!)
            .jsonPath("$.content[1].userId").isEqualTo(reviews[2].userId)
            .jsonPath("$.content[1].storeId").isEqualTo(reviews[2].storeId)
            .jsonPath("$.content[1].orderId").isEqualTo(reviews[2].orderId)
            .jsonPath("$.content[1].storeName").isEqualTo(stores[2].name)
            .jsonPath("$.content[1].userName").isEqualTo(users[2].name)
            .jsonPath("$.content[1].userNickname").isEqualTo(users[2].nickname)
            .jsonPath("$.content[1].userPhoneNumber").isEqualTo(users[2].phoneNumber)
            .jsonPath("$.content[1].score").isEqualTo(reviews[2].score)

            .jsonPath("$.content[2].id").isEqualTo(reviews[1].id!!)
            .jsonPath("$.content[2].userId").isEqualTo(reviews[1].userId)
            .jsonPath("$.content[2].storeId").isEqualTo(reviews[1].storeId)
            .jsonPath("$.content[2].orderId").isEqualTo(reviews[1].orderId)
            .jsonPath("$.content[2].storeName").isEqualTo(stores[1].name)
            .jsonPath("$.content[2].userName").isEqualTo(users[1].name)
            .jsonPath("$.content[2].userNickname").isEqualTo(users[1].nickname)
            .jsonPath("$.content[2].userPhoneNumber").isEqualTo(users[1].phoneNumber)
            .jsonPath("$.content[2].score").isEqualTo(reviews[1].score)

            .jsonPath("$.content[3].id").isEqualTo(reviews[0].id!!)
            .jsonPath("$.content[3].userId").isEqualTo(reviews[0].userId)
            .jsonPath("$.content[3].storeId").isEqualTo(reviews[0].storeId)
            .jsonPath("$.content[3].orderId").isEqualTo(reviews[0].orderId)
            .jsonPath("$.content[3].storeName").isEqualTo(stores[0].name)
            .jsonPath("$.content[3].userName").isEqualTo(users[0].name)
            .jsonPath("$.content[3].userNickname").isEqualTo(users[0].nickname)
            .jsonPath("$.content[3].userPhoneNumber").isEqualTo(users[0].phoneNumber)
            .jsonPath("$.content[3].score").isEqualTo(reviews[0].score)

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(4)
    }

    @DisplayName("관리자는 리뷰 상세조회를 할 수 있다.")
    @Test
    fun getReview() {
        val reviewId = 1L
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
            createdAt = now.minusYears(1),
            updatedAt = now.minusYears(1),
        )
        val review = Review(
            id = reviewId,
            userId = userId,
            storeId = storeId,
            orderId = orderId,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            createdAt = now,
        )
        val reviewPhotos = listOf(
            ReviewPhoto(1L, reviewId, "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-1.jpg", now),
            ReviewPhoto(2L, reviewId, "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-2.jpg", now),
            ReviewPhoto(3L, reviewId, "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-3.jpg", now),
        )
        val orderItems = listOf(
            OrderItem(5L, orderId, 5L, "아메리카노", 4000, 4, now),
            OrderItem(6L, orderId, 7L, "카페라떼", 5000, 3, now),
            OrderItem(7L, orderId, 13L, "스콘", 3000, 1, now),
        )

        coEvery { useCase.getReview(any()) } returns review
        coEvery { reviewPhotoUseCase.getAllByReviewId(any()) } returns reviewPhotos
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { orderItemUseCase.getAllByOrderId(any()) } returns orderItems

        val expectedResponse = ReviewResponse.from(review, user, store, orderItems, reviewPhotos)
        webTestClient.get().uri("/admin/v1/reviews/$reviewId")
            .accept(MediaType.APPLICATION_JSON)
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
            .jsonPath("$.items[0].menuName").isEqualTo(expectedResponse.items[0].menuName)
            .jsonPath("$.items[1].menuName").isEqualTo(expectedResponse.items[1].menuName)
            .jsonPath("$.items[2].menuName").isEqualTo(expectedResponse.items[2].menuName)
            .jsonPath("$.photos[0].photoUrl").isEqualTo(expectedResponse.photos[0].photoUrl)
            .jsonPath("$.photos[1].photoUrl").isEqualTo(expectedResponse.photos[1].photoUrl)
            .jsonPath("$.createdAt").exists()
    }

    @DisplayName("관리자는 회원의 리뷰를 삭제할 수 있다.")
    @Test
    fun deleteReview() {
        val reviewId = 1L

        coEvery { useCase.deleteReview(any()) } returns Unit

        webTestClient.delete().uri("/admin/v1/reviews/$reviewId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.message").isEqualTo("${reviewId}번 리뷰가 정상적으로 삭제되었습니다.")
    }

}
