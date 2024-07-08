package hyuuny.fooddelivery.users.application

import ChangeUserImageUrlRequest
import hyuuny.fooddelivery.users.domain.User
import hyuuny.fooddelivery.users.infrastructure.UserRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeImageUrlUseCaseTest : BehaviorSpec({

    val repository = mockk<UserRepository>()
    val useCase = UserUseCase(repository)

    Given("회원이 자신의 이미지를 변경하면서") {
        val id = 1L
        val now = LocalDateTime.now()
        val user = User(
            id = 1L,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )
        val request =
            ChangeUserImageUrlRequest(imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/star.jpeg")
        coEvery { repository.findById(any()) } returns user
        coEvery { repository.updateImageUrl(any()) } returns Unit

        `when`("직접 이미지를 첨부하면") {
            useCase.changeImageUrl(id, request)

            then("첨부한 이미지로 변경된다.") {
                coEvery { repository.updateImageUrl(any()) }
            }
        }

        `when`("이미지를 첨부하지 않아도") {
            coEvery { repository.updateImageUrl(any()) } returns Unit
            useCase.changeImageUrl(id, ChangeUserImageUrlRequest(imageUrl = null))

            then("정상적으로 이미지를 변경할 수 있다.") {
                coEvery { repository.updateImageUrl(any()) }
            }
        }
    }

})
