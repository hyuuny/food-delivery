package hyuuny.fooddelivery.application.user

import AdminUserSearchCondition
import ChangeUserEmailCommand
import ChangeUserEmailRequest
import ChangeUserImageUrlCommand
import ChangeUserImageUrlRequest
import ChangeUserNameCommand
import ChangeUserNameRequest
import ChangeUserNicknameCommand
import ChangeUserNicknameRequest
import ChangeUserPhoneNumberCommand
import ChangeUserPhoneNumberRequest
import SignUpUserCommand
import SignUpUserRequest
import hyuuny.fooddelivery.domain.user.User
import hyuuny.fooddelivery.infrastructure.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class UserUseCase(
    private val repository: UserRepository,
) {

    suspend fun getUsersByAdminCondition(searchCondition: AdminUserSearchCondition, pageable: Pageable): Page<User> {
        val page = repository.findAllUsers(searchCondition, pageable)
        return PageImpl(page.content, pageable, page.totalElements)
    }

    @Transactional
    suspend fun signUp(request: SignUpUserRequest): User {
        if (repository.existsByEmail(request.email)) throw IllegalArgumentException("중복된 이메일입니다. email: ${request.email} ")
        UserVerifier.verify(request)

        val now = LocalDateTime.now()
        val user = User.handle(
            SignUpUserCommand(
                name = request.name,
                nickname = request.nickname,
                email = request.email,
                phoneNumber = request.phoneNumber,
                imageUrl = request.imageUrl,
                createdAt = now,
                updatedAt = now,
            )
        )
        return repository.insert(user)
    }

    suspend fun getUser(id: Long): User = findUserByIdOrThrow(id)

    @Transactional
    suspend fun changeName(id: Long, request: ChangeUserNameRequest) {
        UserVerifier.verifyName(request.name)
        val user = findUserByIdOrThrow(id)

        val now = LocalDateTime.now()
        user.handle(
            ChangeUserNameCommand(
                name = request.name,
                updatedAt = now,
            )
        )
        repository.updateName(user)
    }

    @Transactional
    suspend fun changeNickname(id: Long, request: ChangeUserNicknameRequest) {
        UserVerifier.verifyNickname(request.nickname)
        val user = findUserByIdOrThrow(id)

        val now = LocalDateTime.now()
        user.handle(
            ChangeUserNicknameCommand(
                nickname = request.nickname,
                updatedAt = now,
            )
        )
        repository.updateNickname(user)
    }

    @Transactional
    suspend fun changeEmail(id: Long, request: ChangeUserEmailRequest) {
        UserVerifier.verifyEmail(request.email)
        val user = findUserByIdOrThrow(id)

        val now = LocalDateTime.now()
        user.handle(
            ChangeUserEmailCommand(
                email = request.email,
                updatedAt = now,
            )
        )
        repository.updateEmail(user)
    }

    @Transactional
    suspend fun changePhoneNumber(id: Long, request: ChangeUserPhoneNumberRequest) {
        UserVerifier.verifyPhoneNumber(request.phoneNumber)
        val user = findUserByIdOrThrow(id)

        val now = LocalDateTime.now()
        user.handle(
            ChangeUserPhoneNumberCommand(
                phoneNumber = request.phoneNumber,
                updatedAt = now,
            )
        )
        repository.updatePhoneNumber(user)
    }

    @Transactional
    suspend fun changeImageUrl(id: Long, request: ChangeUserImageUrlRequest) {
        val user = findUserByIdOrThrow(id)

        val now = LocalDateTime.now()
        user.handle(
            ChangeUserImageUrlCommand(
                imageUrl = request.imageUrl,
                updatedAt = now,
            )
        )
        repository.updateImageUrl(user)
    }

    @Transactional
    suspend fun deleteUser(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("${id}번 회원을 찾을 수 없습니다.")
        repository.delete(id)
    }

    suspend fun existsById(id: Long): Boolean = repository.existsById(id)

    suspend fun getAllByIds(ids: List<Long>): List<User> = repository.findAllByIdIn(ids)

    private suspend fun findUserByIdOrThrow(id: Long): User =
        repository.findById(id) ?: throw NoSuchElementException("${id}번 회원을 찾을 수 없습니다.")

}
