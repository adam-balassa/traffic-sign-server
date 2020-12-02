package hu.bme.aut.trafficsigns.endpoint

import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.model.BoundingBox
import hu.bme.aut.trafficsigns.api.response.model.Classification
import hu.bme.aut.trafficsigns.api.response.model.Coordinates
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.exception.ErrorResponse
import hu.bme.aut.trafficsigns.model.DetectedSign
import hu.bme.aut.trafficsigns.repository.DetectionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8082)
class ImageIT {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var repository: DetectionRepository

    @Captor
    lateinit var savedDetections: ArgumentCaptor<List<DetectedSign>>

    @Test
    fun simpleStaticImageClassification() {
        DetectionServerMock.detectSuccess()

        val result = webTestClient.post().uri("/image/static")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test"}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).isEqualTo("data:image/png;base64,test")
        assertThat(result?.objects).containsExactlyInAnyOrderElementsOf(listOf(
                Detection(BoundingBox(Coordinates(2.0, 2.0), Coordinates(4.0, 6.0)),
                        Classification(0),
                        0.8
                ),
                Detection(BoundingBox(Coordinates(0.0, 0.0), Coordinates(1.0, 1.0)),
                        Classification(42),
                        0.9
                )
        ))
    }


    @Test
    fun randomImageClassification() {
        DetectionServerMock.detectSuccess()
        val result = webTestClient.post().uri("/image/random")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).contains("data:image/png;base64,")
        assertThat(result?.objects).containsExactlyInAnyOrderElementsOf(listOf(
                Detection(BoundingBox(Coordinates(2.0, 2.0), Coordinates(4.0, 6.0)),
                        Classification(0),
                        0.8
                ),
                Detection(BoundingBox(Coordinates(0.0, 0.0), Coordinates(1.0, 1.0)),
                        Classification(42),
                        0.9
                )
        ))
    }

    @Test
    fun emptyClassification() {
        DetectionServerMock.detectEmpty()

        val result = webTestClient.post().uri("/image/static")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test"}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).isEqualTo("data:image/png;base64,test")
        assertThat(result?.objects).isEmpty()
    }

    @Test
    fun failedClassification() {
        DetectionServerMock.detectFail()

        val result = webTestClient.post().uri("/image/static")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test"}""")
                .exchange()
                .expectStatus().isEqualTo(BAD_GATEWAY)
                .expectBody(ErrorResponse::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.errorCode).isEqualTo(2001)
        assertThat(result?.message).isEqualTo("Invalid result from detector server")
    }

    @Test
    fun imageClassificationNoSigns() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(emptyList())

        val result = webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 19.044, "lon": 49.198}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).isEqualTo("data:image/png;base64,test")
        assertThat(result?.objects).containsExactlyInAnyOrderElementsOf(listOf(
                Detection(BoundingBox(Coordinates(2.0, 2.0), Coordinates(4.0, 6.0)),
                        Classification(0),
                        0.8
                ),
                Detection(BoundingBox(Coordinates(0.0, 0.0), Coordinates(1.0, 1.0)),
                        Classification(42),
                        0.9
                )
        ))
    }

    @Test
    fun imageClassificationNoSignsRepository() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(emptyList())

        webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 1.0, "lon": 2.0}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        verify(repository).findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble())
        verify(repository).saveAll(savedDetections.capture())
        verify(repository).deleteAll(emptyList())
        assertThat(savedDetections.value)
                .hasSize(2)
                .allMatch {
                    it.confidence == 0.8 && it.signClass == 0 && it.lat == 1.0 && it.lon == 2.0 ||
                            it.confidence == 0.9 && it.signClass == 42 && it.lat == 1.0 && it.lon == 2.0
                }
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun imageClassificationNewSign() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(DetectedSign().apply {
                    lat = 19.044; lon = 49.198
                    signClass = 30
                    confidence = 0.6
                }))

        val result = webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 19.044, "lon": 49.198}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).isEqualTo("data:image/png;base64,test")
        assertThat(result?.objects).containsExactlyInAnyOrderElementsOf(listOf(
                Detection(BoundingBox(Coordinates(2.0, 2.0), Coordinates(4.0, 6.0)),
                        Classification(0),
                        0.8
                ),
                Detection(BoundingBox(Coordinates(0.0, 0.0), Coordinates(1.0, 1.0)),
                        Classification(42),
                        0.9
                ),
                Detection(null, Classification(30), 0.5)
        ))
    }

    @Test
    fun imageClassificationNewSignRepo() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(DetectedSign().apply {
                    lat = 1.01; lon = 2.01
                    signClass = 30
                    confidence = 0.6
                }))

        webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 1.0, "lon": 2.0}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        verify(repository).findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble())
        verify(repository).saveAll(savedDetections.capture())
        assertThat(savedDetections.value)
                .hasSize(3)
                .allMatch {
                    it.confidence == 0.5 && it.signClass == 30 && it.lat == 1.01 && it.lon == 2.01 ||
                            it.confidence == 0.8 && it.signClass == 0 && it.lat == 1.0 && it.lon == 2.0 ||
                            it.confidence == 0.9 && it.signClass == 42 && it.lat == 1.0 && it.lon == 2.0
                }
        verify(repository).deleteAll(emptyList())
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun imageClassificationSameSign() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(DetectedSign().apply {
                    lat = 19.044; lon = 49.198
                    signClass = 0
                    confidence = 0.6
                }))

        val result = webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 19.044, "lon": 49.198}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).isEqualTo("data:image/png;base64,test")
        assertThat(result?.objects).containsExactlyInAnyOrderElementsOf(listOf(
                Detection(BoundingBox(Coordinates(2.0, 2.0), Coordinates(4.0, 6.0)),
                        Classification(0),
                        0.85
                ),
                Detection(BoundingBox(Coordinates(0.0, 0.0), Coordinates(1.0, 1.0)),
                        Classification(42),
                        0.9
                )
        ))
    }

    @Test
    fun imageClassificationSameSignRepo() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(DetectedSign().apply {
                    lat = 19.044; lon = 49.198
                    signClass = 0
                    confidence = 0.6
                }))

        webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 1.0, "lon": 2.0}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        verify(repository).findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble())
        verify(repository).saveAll(savedDetections.capture())
        assertThat(savedDetections.value)
                .hasSize(2)
                .allMatch {
                    it.confidence == 0.85 && it.signClass == 0 && it.lat == 1.0 && it.lon == 2.0 ||
                            it.confidence == 0.9 && it.signClass == 42 && it.lat == 1.0 && it.lon == 2.0
                }
        verify(repository).deleteAll(emptyList())
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun imageClassificationWrongSign() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(DetectedSign().apply {
                    lat = 19.044; lon = 49.198
                    signClass = 30
                    confidence = 0.45
                }))

        val result = webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 19.044, "lon": 49.198}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).isEqualTo("data:image/png;base64,test")
        assertThat(result?.objects).containsExactlyInAnyOrderElementsOf(listOf(
                Detection(BoundingBox(Coordinates(2.0, 2.0), Coordinates(4.0, 6.0)),
                        Classification(0),
                        0.8
                ),
                Detection(BoundingBox(Coordinates(0.0, 0.0), Coordinates(1.0, 1.0)),
                        Classification(42),
                        0.9
                )
        ))
    }

    @Test
    fun imageClassificationWrongSignRepo() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(DetectedSign().apply {
                    lat = 19.044; lon = 49.198
                    signClass = 30
                    confidence = 0.45
                }))

        webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 1.0, "lon": 2.0}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        verify(repository).findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble())
        verify(repository).saveAll(savedDetections.capture())
        assertThat(savedDetections.value)
                .hasSize(2)
                .allMatch {
                    it.confidence == 0.8 && it.signClass == 0 && it.lat == 1.0 && it.lon == 2.0 ||
                            it.confidence == 0.9 && it.signClass == 42 && it.lat == 1.0 && it.lon == 2.0
                }

        verify(repository).deleteAll(savedDetections.capture())
        assertThat(savedDetections.value)
                .hasSize(1)
                .allSatisfy { assertThat(it.signClass).isEqualTo(30) }
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun imageClassificationComplex() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(
                        DetectedSign().apply {
                            lat = 19.044; lon = 49.198
                            signClass = 30
                            confidence = 0.6
                        },
                        DetectedSign().apply {
                            lat = 19.044; lon = 49.198
                            signClass = 31
                            confidence = 0.45
                        },
                        DetectedSign().apply {
                            lat = 19.044; lon = 49.198
                            signClass = 0
                            confidence = 0.6
                        }
                ))

        val result = webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 19.044, "lon": 49.198}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        assertThat(result).isNotNull
        assertThat(result?.executionTime).isNotEqualTo(0.0)
        assertThat(result?.image?.base64).isEqualTo("data:image/png;base64,test")
        assertThat(result?.objects).containsExactlyInAnyOrderElementsOf(listOf(
                Detection(BoundingBox(Coordinates(2.0, 2.0), Coordinates(4.0, 6.0)),
                        Classification(0),
                        0.85
                ),
                Detection(BoundingBox(Coordinates(0.0, 0.0), Coordinates(1.0, 1.0)),
                        Classification(42),
                        0.9
                ),
                Detection(null, Classification(30), 0.5)
        ))
    }

    @Test
    fun imageClassificationComplexRepo() {
        DetectionServerMock.detectSuccess()
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(listOf(
                        DetectedSign().apply {
                            lat = 1.01; lon = 2.01
                            signClass = 30
                            confidence = 0.6
                        },
                        DetectedSign().apply {
                            lat = 19.044; lon = 49.198
                            signClass = 31
                            confidence = 0.45
                        },
                        DetectedSign().apply {
                            lat = 19.044; lon = 49.198
                            signClass = 0
                            confidence = 0.6
                        }
                ))

        webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": 1.0, "lon": 2.0}""")
                .exchange()
                .expectStatus().isOk
                .expectBody(DetectionResult::class.java)
                .returnResult()
                .responseBody

        verify(repository).findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble())
        verify(repository).saveAll(savedDetections.capture())
        assertThat(savedDetections.value)
                .hasSize(3)
                .allMatch {
                    it.confidence == 0.85 && it.signClass == 0 && it.lat == 1.0 && it.lon == 2.0 ||
                            it.confidence == 0.9 && it.signClass == 42 && it.lat == 1.0 && it.lon == 2.0 ||
                            it.confidence == 0.5 && it.signClass == 30 && it.lat == 1.01 && it.lon == 2.01
                }

        verify(repository).deleteAll(savedDetections.capture())
        assertThat(savedDetections.value)
                .hasSize(1)
                .allSatisfy { assertThat(it.signClass).isEqualTo(31) }
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun staticImageBadRequest() {
        webTestClient.post().uri("/image/static")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"invalid": "request"}""")
                .exchange()
                .expectStatus().isBadRequest
                .expectBody().isEmpty
    }

    @Test
    fun realTimeImageBadRequest() {
        webTestClient.post().uri("/image")
                .contentType(APPLICATION_JSON)
                .bodyValue("""{"image": "test", "lat": null, "lon: 1.0"}""")
                .exchange()
                .expectStatus().isBadRequest
                .expectBody().isEmpty
    }
}