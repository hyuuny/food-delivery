package hyuuny.fooddelivery.users.presentation.admin.v1

import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.BaseIntegrationTest
import hyuuny.fooddelivery.users.application.UserUseCase
import hyuuny.fooddelivery.users.domain.User
import hyuuny.fooddelivery.users.domain.User.Companion.USER_DEFAULT_IMAGE_URL
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import java.time.LocalDateTime

class UserHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: UserUseCase

    @DisplayName("관리자는 회원 목록을 조회할 수 있다.")
    @Test
    fun getUsers() {
        val now = LocalDateTime.now()
        val firstUser = User(
            id = 1L,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )

        val secondUser = User(
            id = 2L,
            name = "김철수",
            nickname = "철스",
            email = "chulsu@gamil.com",
            phoneNumber = "010-3892-1092",
            createdAt = now,
            updatedAt = now,
        )

        val thirdUser = User(
            id = 3L,
            name = "이지우",
            nickname = "이지",
            email = "easy30@gamil.com",
            phoneNumber = "010-5392-1392",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/easy.jpeg",
            createdAt = now,
            updatedAt = now,
        )

        val fourthUser = User(
            id = 4L,
            name = "박성민",
            nickname = "맛있으면최고",
            email = "good390@naver.com",
            phoneNumber = "010-3892-0839",
            createdAt = now,
            updatedAt = now,
        )

        val fifthUser = User(
            id = 5L,
            name = "유현주",
            nickname = "유연한고객",
            email = "fsjoasd@gamil.com",
            phoneNumber = "010-1290-9827",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/flexible.jpeg",
            createdAt = now,
            updatedAt = now,
        )
        val users = listOf(firstUser, secondUser, thirdUser, fourthUser, fifthUser).sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(users, pageable, users.size.toLong())
        coEvery { useCase.getUsersByAdminCondition(any(), any()) } returns page
        webTestClient.get().uri("/admin/v1/users?sort:id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(fifthUser.id!!)
            .jsonPath("$.content[0].userType").isEqualTo(fifthUser.userType.name)
            .jsonPath("$.content[0].name").isEqualTo(fifthUser.name)
            .jsonPath("$.content[0].nickname").isEqualTo(fifthUser.nickname)
            .jsonPath("$.content[0].email").isEqualTo(fifthUser.email)
            .jsonPath("$.content[0].phoneNumber").isEqualTo(fifthUser.phoneNumber)
            .jsonPath("$.content[0].createdAt").exists()

            .jsonPath("$.content[1].id").isEqualTo(fourthUser.id!!)
            .jsonPath("$.content[1].userType").isEqualTo(fourthUser.userType.name)
            .jsonPath("$.content[1].name").isEqualTo(fourthUser.name)
            .jsonPath("$.content[1].nickname").isEqualTo(fourthUser.nickname)
            .jsonPath("$.content[1].email").isEqualTo(fourthUser.email)
            .jsonPath("$.content[1].phoneNumber").isEqualTo(fourthUser.phoneNumber)
            .jsonPath("$.content[1].createdAt").exists()

            .jsonPath("$.content[2].id").isEqualTo(thirdUser.id!!)
            .jsonPath("$.content[2].userType").isEqualTo(thirdUser.userType.name)
            .jsonPath("$.content[2].name").isEqualTo(thirdUser.name)
            .jsonPath("$.content[2].nickname").isEqualTo(thirdUser.nickname)
            .jsonPath("$.content[2].email").isEqualTo(thirdUser.email)
            .jsonPath("$.content[2].phoneNumber").isEqualTo(thirdUser.phoneNumber)
            .jsonPath("$.content[2].createdAt").exists()

            .jsonPath("$.content[3].id").isEqualTo(secondUser.id!!)
            .jsonPath("$.content[3].userType").isEqualTo(secondUser.userType.name)
            .jsonPath("$.content[3].name").isEqualTo(secondUser.name)
            .jsonPath("$.content[3].nickname").isEqualTo(secondUser.nickname)
            .jsonPath("$.content[3].email").isEqualTo(secondUser.email)
            .jsonPath("$.content[3].phoneNumber").isEqualTo(secondUser.phoneNumber)
            .jsonPath("$.content[3].createdAt").exists()

            .jsonPath("$.content[4].id").isEqualTo(firstUser.id!!)
            .jsonPath("$.content[4].userType").isEqualTo(firstUser.userType.name)
            .jsonPath("$.content[4].name").isEqualTo(firstUser.name)
            .jsonPath("$.content[4].nickname").isEqualTo(firstUser.nickname)
            .jsonPath("$.content[4].email").isEqualTo(firstUser.email)
            .jsonPath("$.content[4].phoneNumber").isEqualTo(firstUser.phoneNumber)
            .jsonPath("$.content[4].createdAt").exists()

            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(5)

    }

    @DisplayName("관리자는 기간내에 가입한 회원 목록을 조회할 수 있다.")
    @Test
    fun getUsers_between_createdAt() {
        val firstUser = User(
            id = 1L,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = LocalDateTime.of(2024, 1, 1, 0, 0),
            updatedAt = LocalDateTime.of(2024, 1, 1, 0, 0),
        )

        val secondUser = User(
            id = 2L,
            name = "김철수",
            nickname = "철스",
            email = "chulsu@gamil.com",
            phoneNumber = "010-3892-1092",
            createdAt = LocalDateTime.of(2024, 1, 8, 0, 0),
            updatedAt = LocalDateTime.of(2024, 1, 8, 0, 0),
        )

        val thirdUser = User(
            id = 3L,
            name = "이지우",
            nickname = "이지",
            email = "easy30@gamil.com",
            phoneNumber = "010-5392-1392",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/easy.jpeg",
            createdAt = LocalDateTime.of(2024, 1, 21, 0, 0),
            updatedAt = LocalDateTime.of(2024, 1, 21, 0, 0),
        )

        val fourthUser = User(
            id = 4L,
            name = "박성민",
            nickname = "맛있으면최고",
            email = "good390@naver.com",
            phoneNumber = "010-3892-0839",
            createdAt = LocalDateTime.of(2024, 3, 7, 0, 0),
            updatedAt = LocalDateTime.of(2024, 3, 7, 0, 0),
        )

        val fifthUser = User(
            id = 5L,
            name = "유현주",
            nickname = "유연한고객",
            email = "fsjoasd@gamil.com",
            phoneNumber = "010-1290-9827",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/flexible.jpeg",
            createdAt = LocalDateTime.of(2024, 5, 6, 0, 0),
            updatedAt = LocalDateTime.of(2024, 5, 6, 0, 0),
        )
        val users = listOf(firstUser, secondUser, thirdUser, fourthUser, fifthUser)
            .filter { it.createdAt >= LocalDateTime.of(2024, 1, 1, 0, 0) }
            .filter { it.createdAt <= LocalDateTime.of(2024, 1, 15, 0, 0) }
            .sortedByDescending { it.id }

        val pageable = PageRequest.of(0, 15, Sort.by(Sort.DEFAULT_DIRECTION, "id"))
        val page = PageImpl(users, pageable, users.size.toLong())
        coEvery { useCase.getUsersByAdminCondition(any(), any()) } returns page

        webTestClient.get().uri("/admin/v1/users?fromDate=2024-01-01&toDate=2024-01-15&sort:id:desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.content[0].id").isEqualTo(secondUser.id!!)
            .jsonPath("$.content[0].userType").isEqualTo(secondUser.userType.name)
            .jsonPath("$.content[0].name").isEqualTo(secondUser.name)
            .jsonPath("$.content[0].nickname").isEqualTo(secondUser.nickname)
            .jsonPath("$.content[0].email").isEqualTo(secondUser.email)
            .jsonPath("$.content[0].phoneNumber").isEqualTo(secondUser.phoneNumber)
            .jsonPath("$.content[0].createdAt").exists()
            .jsonPath("$.content[1].id").isEqualTo(firstUser.id!!)
            .jsonPath("$.content[1].name").isEqualTo(firstUser.name)
            .jsonPath("$.content[1].nickname").isEqualTo(firstUser.nickname)
            .jsonPath("$.content[1].email").isEqualTo(firstUser.email)
            .jsonPath("$.content[1].phoneNumber").isEqualTo(firstUser.phoneNumber)
            .jsonPath("$.content[1].createdAt").exists()
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.size").isEqualTo(15)
            .jsonPath("$.last").isEqualTo(true)
            .jsonPath("$.totalElements").isEqualTo(2)
    }

    @DisplayName("관리자는 회원을 상세조회할 수 있다.")
    @Test
    fun getUser() {
        val id = 1L
        val now = LocalDateTime.now()
        val user = User(
            id = id,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            imageUrl = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/hyuuny.jpeg",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { useCase.getUser(any()) } returns user

        webTestClient.get().uri("/admin/v1/users/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(user.id!!)
            .jsonPath("$.userType").isEqualTo(user.userType.name)
            .jsonPath("$.name").isEqualTo(user.name)
            .jsonPath("$.nickname").isEqualTo(user.nickname)
            .jsonPath("$.email").isEqualTo(user.email)
            .jsonPath("$.phoneNumber").isEqualTo(user.phoneNumber)
            .jsonPath("$.imageUrl").isEqualTo(user.imageUrl!!)
            .jsonPath("$.createdAt").exists()
    }

    @DisplayName("관리자가 회원을 상세조회했을 때, 등록된 이미지가 없으면 기본 이미지로 노출된다.")
    @Test
    fun getUser_default_imageUrl() {
        val id = 1L
        val now = LocalDateTime.now()
        val user = User(
            id = id,
            name = "김성현",
            nickname = "hyuuny",
            email = "shyune@knou.ac.kr",
            phoneNumber = "010-1234-1234",
            createdAt = now,
            updatedAt = now,
        )
        coEvery { useCase.getUser(any()) } returns user

        webTestClient.get().uri("/admin/v1/users/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(user.id!!)
            .jsonPath("$.userType").isEqualTo(user.userType.name)
            .jsonPath("$.name").isEqualTo(user.name)
            .jsonPath("$.nickname").isEqualTo(user.nickname)
            .jsonPath("$.email").isEqualTo(user.email)
            .jsonPath("$.phoneNumber").isEqualTo(user.phoneNumber)
            .jsonPath("$.imageUrl").isEqualTo(USER_DEFAULT_IMAGE_URL)
            .jsonPath("$.createdAt").exists()
    }

}
