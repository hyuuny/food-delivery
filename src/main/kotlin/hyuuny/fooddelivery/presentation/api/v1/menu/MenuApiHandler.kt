package hyuuny.fooddelivery.presentation.api.v1.menu

import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.presentation.api.v1.menu.response.MenuResponse
import hyuuny.fooddelivery.presentation.api.v1.menu.response.OptionGroupResponse
import hyuuny.fooddelivery.presentation.api.v1.menu.response.OptionResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class MenuApiHandler(
    private val useCase: MenuUseCase,
    private val optionGroupUseCase: OptionGroupUseCase,
    private val optionUseCase: OptionUseCase,
) {

    suspend fun getMenu(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        return coroutineScope {
            val menuDeferred = async { useCase.getMenu(id) }
            val optionGroupsDeferred = async { optionGroupUseCase.getAllByMenuId(id).sortedBy { it.priority } }

            val optionGroups = optionGroupsDeferred.await()
            val optionsDeferred = async { optionUseCase.getAllByOptionGroupIds(optionGroups.mapNotNull { it.id }) }

            val menu = menuDeferred.await()
            val options = optionsDeferred.await()
            val optionMap = options
                .sortedWith(compareBy<Option> { it.price }.thenByDescending { it.id })
                .groupBy { it.optionGroupId }

            val optionGroupResponse = optionGroups.map { group ->
                val optionsOfOptionGroup = optionMap[group.id]?.map { OptionResponse.from(it) } ?: emptyList()
                OptionGroupResponse.from(group, optionsOfOptionGroup)
            }

            val response = MenuResponse.from(menu, optionGroupResponse)
            ok().bodyValueAndAwait(response)
        }
    }

}
