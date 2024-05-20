package hyuuny.fooddelivery.presentation.admin.v1

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
@SpringBootTest
@AutoConfigureWebTestClient
class BaseIntegrationTest {

    @Autowired
    protected lateinit var webTestClient: WebTestClient

}
