package hyuuny.fooddelivery.handler

import hyuuny.fooddelivery.presentation.admin.v1.Routes
import hyuuny.fooddelivery.presentation.admin.v1.menu.MenuHandler
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
@WebFluxTest(
    value = [
        MenuHandler::class,
    ]

)
@Import(Routes::class)
open class BaseIntegrationTest {

    @Autowired
    protected lateinit var webTestClient: WebTestClient

}