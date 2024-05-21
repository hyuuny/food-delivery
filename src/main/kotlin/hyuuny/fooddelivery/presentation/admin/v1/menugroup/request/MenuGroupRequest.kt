data class CreateMenuGroupRequest(
    val menuId: Long,
    val name: String,
    val required: Boolean,
    val priority: Int,
)

data class UpdateMenuGroupRequest(
    val menuId: Long,
    val name: String,
    val required: Boolean,
)

data class ReorderMenuGroupRequests(
    val reOrderedMenuGroups: List<ReorderMenuGroupRequest>,
)

data class ReorderMenuGroupRequest(
    val menuGroupId: Long,
    val priority: Int,
)

data class MenuGroupSearchCondition(
    val menuId: Long? = null,
    val name: String? = null,
)

