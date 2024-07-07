package hyuuny.fooddelivery.application.likedstore

import LikeOrCancelRequest
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.application.user.UserUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.domain.likedstore.LikedStore
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.likedstore.LikedStoreRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

internal class LikeOrCancelUseCaseTest : BehaviorSpec({

    val repository = mockk<LikedStoreRepository>()
    val useCase = LikedStoreUseCase(repository)
    val userUseCase = mockk<UserUseCase>()
    val storeUseCase = mockk<StoreUseCase>()

    given("매장의 찜을 눌렀을 때") {
        val userId = 1L
        val storeId = 77L

        val request = LikeOrCancelRequest(
            userId = userId,
            storeId = storeId
        )

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
        val likedStore = LikedStore(
            id = 1L,
            userId = userId,
            storeId = storeId,
            createdAt = now
        )
        coEvery { userUseCase.getUser(any()) } returns user
        coEvery { storeUseCase.getStore(any()) } returns store

        `when`("찜하지 않은 매장이면") {
            coEvery { repository.findByUserIdAndStoreId(any(), any()) } returns null
            coEvery { repository.insert(any()) } returns likedStore
            useCase.likeOrCancel(request, { user }, { store })

            then("찜할 수 있다.") {
                coVerify { repository.insert(any()) }
            }
        }

        `when`("이미 찜한 매장이면") {
            coEvery { repository.findByUserIdAndStoreId(any(), any()) } returns likedStore
            coEvery { repository.delete(any()) } returns Unit
            useCase.likeOrCancel(request, { user }, { store })

            then("찜을 취소할 수 있다.") {
                coVerify { repository.delete(any()) }
            }
        }

        `when`("존재하지 않는 회원이면") {
            coEvery { userUseCase.getUser(any()) } throws NoSuchElementException("0번 회원을 찾을 수 없습니다.")

            then("회원을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.likeOrCancel(request, { userUseCase.getUser(0) }, { store })
                }
                ex.message shouldBe "0번 회원을 찾을 수 없습니다."
            }
        }

        `when`("존재하지 않는 매장이면") {
            coEvery { storeUseCase.getStore(any()) } throws NoSuchElementException("0번 매장을 찾을 수 없습니다.")

            then("매장을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.likeOrCancel(request, { user }, { storeUseCase.getStore(0) })
                }
                ex.message shouldBe "0번 매장을 찾을 수 없습니다."
            }
        }
    }
})
