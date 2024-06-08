package hyuuny.fooddelivery.application.category

import hyuuny.fooddelivery.infrastructure.category.CategoryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

internal class DeleteCategoryGroupUseCaseTest : BehaviorSpec({

    val repository = mockk<CategoryRepository>()
    val useCase = CategoryUseCase(repository)

    given("카테고리를 삭제 할 때") {
        val categoryId = 1L
        coEvery { repository.existsById(any()) } returns true
        coEvery { repository.delete(any()) } returns Unit

        `when`("존재하는 카테고리이면") {
            useCase.deleteCategory(categoryId)

            then("정상적으로 카테고리를 삭제할 수 있다.") {
                coVerify { repository.delete(any()) }
            }
        }

        `when`("존재하지 않는 카테고리이면") {
            coEvery { repository.existsById(any()) } returns false

            then("존재하지 않는 카테고리라는 메세지가 반환된다.") {
                val ex = shouldThrow<NoSuchElementException> {
                    useCase.deleteCategory(0)
                }
                ex.message shouldBe "존재하지 않는 카테고리입니다."
            }
        }
    }
})