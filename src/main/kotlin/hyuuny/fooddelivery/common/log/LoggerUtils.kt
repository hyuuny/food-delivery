package hyuuny.fooddelivery.common.log

import org.slf4j.Logger

object LoggerUtils : Log {

    private val logger: Logger
        get() = this.log

    fun logRequestDetails(method: String, uri: String, message: String) {
        logger.info("[$method $uri] $message")
    }

}
