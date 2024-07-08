package hyuuny.fooddelivery.menus.presentation.api.v1.response

import hyuuny.fooddelivery.menus.domain.Menu
import hyuuny.fooddelivery.optiongroups.application.OptionGroupUseCase
import hyuuny.fooddelivery.options.application.OptionUseCase
import hyuuny.fooddelivery.options.domain.Option
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
