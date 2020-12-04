package hu.bme.aut.trafficsigns.endpoint

import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.api.response.model.LocatedDetection
import hu.bme.aut.trafficsigns.model.DetectedSign
import hu.bme.aut.trafficsigns.repository.DetectionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8082)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = ["delete from detections"])
class StoredDetectionsIT {
    @Autowired
    lateinit var webTestClient: WebTestClient


    @Test
    fun getAllSignsAfterDetection() {
        setupDetection()

        val resultList = webTestClient.get().uri("/signs")
                .exchange()
                .expectStatus().isOk
                .expectBodyList(LocatedDetection::class.java)
                .returnResult()
                .responseBody
        assertThat(resultList)
                .hasSize(2)
                .allMatch {
                    it.lat == 1.0 && it.lon == 2.0 && (
                            it.confidence == 0.8 && it.classification?.serial == 0 ||
                                    it.confidence == 0.9 && it.classification?.serial == 42
                            )
                }
    }

    @Test
    fun getSignsByPositionAfterDetection() {
        setupDetection()

        val resultList = webTestClient.get().uri {
            it.path("/signs")
                    .queryParam("lat", 1.01)
                    .queryParam("lon", 2.01)
                    .queryParam("radius", 0.01)
                    .build()
        }.exchange()
                .expectStatus().isOk
                .expectBodyList(LocatedDetection::class.java)
                .returnResult()
                .responseBody

        assertThat(resultList)
        assertThat(resultList)
                .hasSize(2)
                .allMatch {
                    it.lat == 1.0 && it.lon == 2.0 && (
                            it.confidence == 0.8 && it.classification?.serial == 0 ||
                                    it.confidence == 0.9 && it.classification?.serial == 42
                            )
                }
    }

    @Test
    fun getSignsBadRequest() {
        webTestClient.get().uri {
            it.path("/signs")
                    .queryParam("lat", 1.01)
                    .queryParam("lon", 2.01)
                    .build()
        }.exchange()
                .expectStatus().isBadRequest
                .expectBody().isEmpty
    }

    private fun setupDetection() {
        DetectionServerMock.detectSuccess()

        webTestClient.post().uri("/image")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 1.0, "lon": 2.0}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody
    }
}