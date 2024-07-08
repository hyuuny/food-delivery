package hyuuny.fooddelivery.stores.application

import hyuuny.fooddelivery.stores.infrastructure.StoreDetailRepository
import hyuuny.fooddelivery.stores.infrastructure.StoreImageRepository
import hyuuny.fooddelivery.stores.infrastructure.StoreRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class DeleteStoreUseCaseTest : BehaviorSpec({

    val repository = mockk<StoreRepository>()
    val detailRepository = mockk<StoreDetailRepository>()
    val imageRepository = mockk<StoreImageRepository>()
    val useCase = StoreUseCase(repository, detailRepository, imageRepository)

    Given("매장을 삭제할 때") {
        val storeId = 1L
        coEvery { repository.existsById(any()) } returns true
        coEvery { imageRepository.deleteAllByStoreId(any()) } returns Unit
        coEvery { detailRepository.deleteByStoreId(any()) } returns Unit
        coEvery { repository.delete(any()) } returns Unit

        `when`("존재하는 매장이면") {
            useCase.deleteStore(storeId)

            then("정상적으로 매장을 삭제할 수 있다.") {
                coEvery { repository.delete(any()) }
            }
        }

        `when`("존재하지 않는 매장 아이디라면") {
            coEvery { repository.existsById(any()) } returns false

            then("매장을 찾을 수 없다는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteStore(0)
                }
                ex.message shouldBe "0번 매장을 찾을 수 없습니다."
            }
        }
    }

})
