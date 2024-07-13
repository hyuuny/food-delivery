package hyuuny.fooddelivery.deliveries.application

import ChangeDeliveryStatusRequest
import hyuuny.fooddelivery.common.constant.DeliveryStatus
import hyuuny.fooddelivery.deliveries.domain.Delivery
import hyuuny.fooddelivery.deliveries.infrastructure.DeliveryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime

class ChangeDeliveryStatusUseCaseTest : BehaviorSpec({

    val repository = mockk<DeliveryRepository>()
    val useCase = DeliveryUseCase(repository)

    Given("배달 내역 상태를 변경할 때") {
        val orderId = 1L
        val riderId = 293L

        val now = LocalDateTime.now()
        val request = ChangeDeliveryStatusRequest(
            status = DeliveryStatus.CANCELED
        )
        val delivery = Delivery(
            id = 1L,
            orderId = orderId,
            riderId = riderId,
            status = DeliveryStatus.DELIVERED,
            createdAt = now,
        )
        coEvery { repository.findById(any()) } returns delivery
        coEvery { repository.updateStatus(any()) } returns Unit

        When("존재하는 배달내역이면") {
            useCase.changeDeliverStatus(delivery.id!!, request)

            Then("상태를 변경할 수 있다.") {
                coVerify { repository.updateStatus(any()) }
            }

            When("존재하지 않는 배달내역이면") {
                coEvery { repository.findById(any()) } returns null

                Then("배달 내역을 찾을 수 없다는 메세지가 반환된다.") {
                    val ex = shouldThrow<NoSuchElementException> {
                        useCase.getDelivery(0)
                    }
                    ex.message shouldBe "0번 배달 내역을 찾을 수 없습니다."
                }
            }
        }
    }
})
