import org.springframework.data.domain.Sort
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull

fun extractCursorAndCount(request: ServerRequest, defaultCursor: Int = 0, defaultCount: Int = 15): Pair<Int, Int> {
    val cursor = request.queryParamOrNull("cursor")?.toIntOrNull() ?: defaultCursor
    val count = request.queryParamOrNull("count")?.toIntOrNull() ?: defaultCount
    return cursor to count
}

// sort=price:asc,id:desc
fun parseSort(sortParam: String?): Sort {
    return sortParam?.let {
        it.split(",").map { sortField ->
            val splitParam = sortField.split(":")
            val property = splitParam[0]
            val direction = if (splitParam.getOrNull(1) == "asc") Sort.Direction.ASC else Sort.Direction.DESC
            Sort.Order(direction, property)
        }.let { orders ->
            Sort.by(orders)
        }
    } ?: Sort.by(Sort.Direction.DESC, "id")
}

fun parseBooleanQueryParam(queryParam: String?): Boolean? = queryParam?.let { param ->
    param.lowercase().takeIf { it == "true" || it == "false" }?.toBoolean()
}