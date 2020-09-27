package hu.bme.aut.trafficsigns.endpoint

import hu.bme.aut.trafficsigns.api.response.DetectionResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ImageIT {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun simpleImageClassification() {
        val result = webTestClient.post().uri("/image")
                .bodyValue("test")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
    }

    @Test
    fun randomImageClassification() {
        val result = webTestClient.post().uri("/image/random")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
    }
}