package hyuuny.fooddelivery.useraddresses.infrastructure

import hyuuny.fooddelivery.useraddresses.domain.UserAddress

interface UserAddressRepository {

    suspend fun insert(userAddress: UserAddress): UserAddress

    suspend fun findById(id: Long): UserAddress?

    suspend fun findAllByUserId(userId: Long): List<UserAddress>

    suspend fun update(userAddress: UserAddress)

    suspend fun updateSelectedAddresses(userAddress: List<UserAddress>)

    suspend fun delete(id: Long)

}
