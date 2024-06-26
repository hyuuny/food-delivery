package hyuuny.fooddelivery.application.review

import CreateReviewPhotoRequest
import CreateReviewRequest
import generateOrderNumber
import generatePaymentId
import hyuuny.fooddelivery.application.order.OrderUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.OrderStatus
import hyuuny.fooddelivery.common.constant.PaymentMethod
import hyuuny.fooddelivery.domain.order.Order
import hyuuny.fooddelivery.domain.review.Review
import hyuuny.fooddelivery.domain.review.ReviewPhoto
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.review.ReviewPhotoRepository
import hyuuny.fooddelivery.infrastructure.review.ReviewRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class CreateOrderUseCaseTest : BehaviorSpec({

    val repository = mockk<ReviewRepository>()
    val reviewPhotoRepository = mockk<ReviewPhotoRepository>()
    val userUseCase = mockk<UserUseCase>()
    val storeUseCase = mockk<StoreUseCase>()
    val orderUseCase = mockk<OrderUseCase>()
    val useCase = ReviewUseCase(repository, reviewPhotoRepository)

    Given("회원이 리뷰를 작성할 때") {
        val userId = 1L
        val storeId = 1L
        val orderId = 1L
        val reviewId = 1L

        val now = LocalDateTime.now()
        val user = User(
            id = userId,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now.minusMonths(5),
            updatedAt = now.minusMonths(5),
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

        val order = Order(
            id = orderId,
            orderNumber = generateOrderNumber(now),
            userId = userId,
            storeId = storeId,
            categoryId = 1L,
            paymentId = generatePaymentId(),
            paymentMethod = PaymentMethod.NAVER_PAY,
            status = OrderStatus.CREATED,
            deliveryType = DeliveryType.OUTSOURCING,
            zipCode = "12345",
            address = "서울시 강남구 역삼동",
            detailAddress = "위워크 빌딩 19층",
            phoneNumber = "010-1234-5678",
            messageToRider = "1층 로비에 보관 부탁드립니다",
            messageToStore = null,
            totalPrice = 20000,
            deliveryFee = 0,
            createdAt = now,
            updatedAt = now,
        )

        val request = CreateReviewRequest(
            storeId = storeId,
            orderId = 1L,
            score = 5,
            content = "맛있어요. 다음에 또 주문할게요.",
            photos = listOf(
                CreateReviewPhotoRequest("https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-1.jpg"),
                CreateReviewPhotoRequest("https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-2.jpg"),
                CreateReviewPhotoRequest("https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/review-3.jpg"),
            )
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
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { orderUseCase.getOrder(any()) } returns order
        coEvery { repository.existsByUserIdAndOrderId(any(), any()) } returns false
        coEvery { reviewPhotoRepository.insertAll(any()) } returns reviewPhotos
        coEvery { repository.insert(any()) } returns review

        `when`("유효한 리뷰를 작성하면") {
            val result = useCase.createReview(
                request = request,
                getUser = { userUseCase.getUser(userId) },
                getStore = storeUseCase::getStore,
                getOrder = orderUseCase::getOrder,
            )

            then("리뷰가 정상적으로 생성된다") {
                result.id.shouldNotBeNull()
                result.userId shouldBe userId
                result.storeId shouldBe request.storeId
                result.orderId shouldBe request.orderId
                result.score shouldBe request.score
                result.content shouldBe request.content
                result.createdAt.shouldNotBeNull()
            }
        }

        `when`("평점이 1~5 사이가 아니면") {

            then("잘못된 리뷰 평점이라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createReview(
                        request = request.copy(score = 6),
                        getUser = { userUseCase.getUser(userId) },
                        getStore = storeUseCase::getStore,
                        getOrder = orderUseCase::getOrder,
                    )
                }
                ex.message shouldBe "잘못된 리뷰 평점입니다."
            }
        }

        `when`("존재하지 않는 회원이면") {
            coEvery { userUseCase.getUser(any()) } throws NoSuchElementException("0번 회원을 찾을 수 없습니다.")

            then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createReview(
                        request = request,
                        getUser = { userUseCase.getUser(0) },
                        getStore = storeUseCase::getStore,
                        getOrder = orderUseCase::getOrder,
                    )
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 매장이면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { storeUseCase.getStore(any()) } throws NoSuchElementException("0번 매장을 찾을 수 없습니다.")

            then("매장을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createReview(
                        request = request.copy(storeId = 0),
                        getUser = { userUseCase.getUser(userId) },
                        getStore = storeUseCase::getStore,
                        getOrder = orderUseCase::getOrder,
                    )
                }
                ex.message shouldBe "0번 매장을 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 주문이면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { storeUseCase.getStore(any()) } returns store
            coEvery { orderUseCase.getOrder(any()) } throws NoSuchElementException("0번 주문을 찾을 수 없습니다.")

            then("주문을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.createReview(
                        request = request.copy(orderId = 0),
                        getUser = { userUseCase.getUser(userId) },
                        getStore = storeUseCase::getStore,
                        getOrder = orderUseCase::getOrder,
                    )
                }
                ex.message shouldBe "0번 주문을 찾을 수 없습니다."
            }
        }

        `when`("리뷰의 매장아이디와 주문한 매장의 아이디가 서로 다르면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { storeUseCase.getStore(any()) } returns store
            coEvery { orderUseCase.getOrder(any()) } returns order

            then("매장과 주문한 매장이 서로 다르다는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createReview(
                        request = request.copy(storeId = 1029),
                        getUser = { userUseCase.getUser(userId) },
                        getStore = storeUseCase::getStore,
                        getOrder = orderUseCase::getOrder,
                    )
                }
                ex.message shouldBe "매장과 주문한 매장이 서로 다릅니다."
            }
        }

        `when`("주문에 이미 작성한 리뷰를 중복 작성하면") {
            coEvery { userUseCase.getUser(any()) } returns user
            coEvery { storeUseCase.getStore(any()) } returns store
            coEvery { orderUseCase.getOrder(any()) } returns order
            coEvery { repository.existsByUserIdAndOrderId(any(), any()) } returns true

            then("이미 등록된 리뷰라는 메세지가 반환된다.") {
                val ex = shouldThrow<IllegalArgumentException> {
                    useCase.createReview(
                        request = request,
                        getUser = { userUseCase.getUser(userId) },
                        getStore = storeUseCase::getStore,
                        getOrder = orderUseCase::getOrder,
                    )
                }
                ex.message shouldBe "이미 등록된 리뷰입니다."
            }
        }
    }

})
