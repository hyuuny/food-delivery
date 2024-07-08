data class CreateReviewRequest(
    val storeId: Long,
    val orderId: Long,
    val score: Int,
    val content: String,
    val photos: List<CreateReviewPhotoRequest>,
)

data class CreateReviewPhotoRequest(
    val photoUrl: String,
)

data class ApiReviewSearchCondition(
    val userId: Long?,
    val storeId: Long?,
)
