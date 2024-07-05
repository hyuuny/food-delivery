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
    val menuId: Long,
    val reOrderedOptionGroups: List<ReorderOptionGroupRequest>,
)

data class ReorderOptionGroupRequest(
    val optionGroupId: Long,
    val priority: Int,
)

data class AdminOptionGroupSearchCondition(
    val menuId: Long? = null,
    val name: String? = null,
)

