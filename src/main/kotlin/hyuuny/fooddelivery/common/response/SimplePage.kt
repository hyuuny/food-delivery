package hyuuny.fooddelivery.common.response

import org.springframework.data.domain.Page

class SimplePage<T>(
    val content: List<T>,
    val pageNumber: Int,
    val size: Int,
    val last: Boolean,
    val totalElements: Long,
) {
    constructor(content: List<T>, page: Page<*>) : this(
        content = content,
        pageNumber = page.number + 1,
        size = page.size,
        last = page.isLast,
        totalElements = page.totalElements,
    )
}
