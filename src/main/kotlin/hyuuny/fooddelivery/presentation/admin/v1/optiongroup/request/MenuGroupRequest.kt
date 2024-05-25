data class CreateOptionGroupRequest(
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val priority: Int,
)

data class UpdateOptionGroupRequest(
    val menuId: Long,
    val name: String,
    val required: Boolean,
)

data class ReorderOptionGroupRequests(
    val reOrderedOptionGroups: List<ReorderOptionGroupRequest>,
)

data class ReorderOptionGroupRequest(
    val optionGroupId: Long,
    val priority: Int,
)

data class OptionGroupSearchCondition(
    val menuId: Long? = null,
    val name: String? = null,
)

