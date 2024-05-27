import kotlinx.coroutines.reactive.awaitFirstOrElse
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query


suspend inline fun <reified T : Any> R2dbcEntityTemplate.selectAndCount(
    query: Query,
    criteria: Criteria
): Pair<List<T>, Long> {
    return selectAll<T>(query) to countAll<T>(criteria)
}

suspend inline fun <reified T : Any> R2dbcEntityTemplate.selectAll(query: Query): List<T> {
    return this.select(T::class.java)
        .matching(query)
        .all()
        .collectList()
        .awaitFirstOrElse { emptyList() }
}

suspend inline fun <reified T : Any> R2dbcEntityTemplate.countAll(criteria: Criteria): Long {
    return this.select(T::class.java)
        .matching(Query.query(criteria))
        .count()
        .awaitFirstOrElse { 0 }
}
