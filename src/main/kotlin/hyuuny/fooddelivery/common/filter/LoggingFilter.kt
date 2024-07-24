package hyuuny.fooddelivery.common.filter

import hyuuny.fooddelivery.common.log.LoggerUtils
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class LoggingFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> = mono {
        val request = exchange.request
        val method = request.method.name()
        val uri = request.uri.toString()

        LoggerUtils.logRequestDetails(method, uri, "Request started")
        chain.filter(exchange).awaitFirstOrNull()
        LoggerUtils.logRequestDetails(method, uri, "Response finished")
    }.then()

}

