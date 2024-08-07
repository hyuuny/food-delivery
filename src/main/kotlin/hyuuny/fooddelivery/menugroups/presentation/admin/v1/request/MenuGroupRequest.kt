data class CreateMenuGroupRequest(
    val storeId: Long,
    val name: String,
    val priority: Int,
    val description: String? = null,
)

data class UpdateMenuGroupRequest(
    val name: String,
    val description: String? = null,
)

data class ReorderMenuGroupRequests(
    val storeId: Long,
    val reOrderedMenuGroups: List<ReorderMenuGroupRequest>,
)

data class ReorderMenuGroupRequest(
    val menuGroupId: Long,
    val priority: Int,
)

data class AdminMenuGroupSearchCondition(
    val id: Long? = null,
    val storeId: Long? = null,
    val name: String? = null,
)
