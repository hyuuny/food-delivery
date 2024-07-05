package hyuuny.fooddelivery.presentation.api.v1.menu

import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.optiongroup.OptionGroupUseCase
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.presentation.api.v1.menu.response.MenuResponse
import hyuuny.fooddelivery.presentation.api.v1.menu.response.OptionGroupResponse
import hyuuny.fooddelivery.presentation.api.v1.menu.response.OptionResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class MenuResponseMapper(
    private val optionGroupUseCase: OptionGroupUseCase,
    private val optionUseCase: OptionUseCase,
) {
    suspend fun mepToMenuResponse(menu: Menu): MenuResponse = coroutineScope {
        val optionGroupsDeferred = async { optionGroupUseCase.getAllByMenuId(menu.id!!).sortedBy { it.priority } }
        val optionGroups = optionGroupsDeferred.await()

        val optionsDeferred = async { optionUseCase.getAllByOptionGroupIds(optionGroups.mapNotNull { it.id }) }
        val options = optionsDeferred.await()

        val optionMap = options
            .sortedWith(compareBy<Option> { it.price }.thenByDescending { it.id })
            .groupBy { it.optionGroupId }

        val optionGroupResponse = optionGroups.map { group ->
            val optionsOfOptionGroup = optionMap[group.id]?.map { OptionResponse.from(it) } ?: emptyList()
            OptionGroupResponse.from(group, optionsOfOptionGroup)
        }
        MenuResponse.from(menu, optionGroupResponse)
    }
}
