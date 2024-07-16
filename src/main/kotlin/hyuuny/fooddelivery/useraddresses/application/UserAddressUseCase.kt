package hyuuny.fooddelivery.useraddresses.application

import ChangeUserAddressSelectedCommand
import CreateUserAddressCommand
import UpdateUserAddressCommand
import hyuuny.fooddelivery.useraddresses.domain.UserAddress
import hyuuny.fooddelivery.useraddresses.domain.UserAddress.Companion.MAX_USER_ADDRESS_COUNT
import hyuuny.fooddelivery.useraddresses.infrastructure.UserAddressRepository
import hyuuny.fooddelivery.useraddresses.presentation.api.v1.request.CreateUserAddressRequest
import hyuuny.fooddelivery.useraddresses.presentation.api.v1.request.UpdateUserAddressRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class UserAddressUseCase(
    private val repository: UserAddressRepository,
) {

    @Transactional
    suspend fun createUserAddress(userId: Long, request: CreateUserAddressRequest): UserAddress {
        val now = LocalDateTime.now()

        val userAddresses = repository.findAllByUserId(userId)
        if (userAddresses.size >= MAX_USER_ADDRESS_COUNT) throw IllegalArgumentException("주소는 최대 10개까지만 등록할 수 있습니다.")

        updateExistingAddressesSelectedToFalse(userId, now)
        val newUserAddress = UserAddress.handle(
            CreateUserAddressCommand(
                userId = userId,
                name = request.name,
                zipCode = request.zipCode,
                address = request.address,
                detailAddress = request.detailAddress,
                messageToRider = request.messageToRider,
                entrancePassword = request.entrancePassword,
                routeGuidance = request.routeGuidance,
                selected = true,
                createdAt = now,
                updatedAt = now,
            )
        )
        return repository.insert(newUserAddress)
    }

    suspend fun getAllUserAddress(userId: Long): List<UserAddress> = repository.findAllByUserId(userId)

    suspend fun getUserAddress(id: Long): UserAddress = findUserAddressByIdOrThrow(id)

    @Transactional
    suspend fun updateUserAddress(id: Long, request: UpdateUserAddressRequest) {
        val now = LocalDateTime.now()
        val userAddress = findUserAddressByIdOrThrow(id)

        userAddress.handle(
            UpdateUserAddressCommand(
                name = request.name,
                zipCode = request.zipCode,
                address = request.address,
                detailAddress = request.detailAddress,
                messageToRider = request.messageToRider,
                entrancePassword = request.entrancePassword,
                routeGuidance = request.routeGuidance,
                updatedAt = now
            )
        )
        repository.update(userAddress)
    }

    @Transactional
    suspend fun changeUserAddressSelectedToTrue(userId: Long, id: Long) {
        val now = LocalDateTime.now()
        val userAddress = findUserAddressByIdOrThrow(id)

        updateExistingAddressesSelectedToFalse(userId, now)
        userAddress.handle(
            ChangeUserAddressSelectedCommand(
                selected = true,
                updatedAt = now
            )
        )
        repository.updateSelectedAddresses(listOf(userAddress))
    }

    @Transactional
    suspend fun deleteUserAddress(id: Long) {
        val userAddress = findUserAddressByIdOrThrow(id)
        repository.delete(userAddress.id!!)
    }

    private suspend fun findUserAddressByIdOrThrow(id: Long) =
        repository.findById(id) ?: throw NoSuchElementException("회원의 ${id}번 주소를 찾을 수 없습니다.")

    private suspend fun updateExistingAddressesSelectedToFalse(userId: Long, updatedAt: LocalDateTime) {
        val userAddresses = repository.findAllByUserId(userId)
        userAddresses.forEach { userAddress ->
            userAddress.handle(ChangeUserAddressSelectedCommand(selected = false, updatedAt = updatedAt))
        }
        repository.updateSelectedAddresses(userAddresses)
    }

}