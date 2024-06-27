package hyuuny.fooddelivery.presentation.admin.v1.review

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.review.ReviewUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
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
    private lateinit var userUseCase: UserUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

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

}
