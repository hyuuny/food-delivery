package hyuuny.fooddelivery.orders.application

import hyuuny.fooddelivery.orders.domain.OrderItemOption
import hyuuny.fooddelivery.orders.infrastructure.OrderItemOptionRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDateTime

class GetOrderItemOptionUseCaseTest : BehaviorSpec({

    val repository = mockk<OrderItemOptionRepository>()
    val useCase = OrderItemOptionUseCase(repository)

    Given("주문내역에 포함된") {
        val orderItemId = 1L

        val now = LocalDateTime.now()
        val orderItemOptions = listOf(
            OrderItemOption(1L, orderItemId, 1L, "페퍼로니", 1000, now),
            OrderItemOption(2L, orderItemId, 2L, "제로콜라", 1500, now),
            OrderItemOption(3L, orderItemId, 3L, "콘 아이스크림", 500, now),
        )
        coEvery { useCase.getAllByOrderItemIdIn(any()) } returns orderItemOptions

        `when`("주문 항목의 옵션들을") {
            val result = useCase.getAllByOrderItemIdIn(listOf(orderItemId))

            then("조회할 수 있다.") {
                result.forEachIndexed { idx, orderItem ->
                    orderItem.id shouldBe orderItemOptions[idx].id
                    orderItem.orderItemId shouldBe orderItemOptions[idx].orderItemId
                    orderItem.optionId shouldBe orderItemOptions[idx].optionId
                    orderItem.optionName shouldBe orderItemOptions[idx].optionName
                    orderItem.optionPrice shouldBe orderItemOptions[idx].optionPrice
                }
            }
        }
    }

})
