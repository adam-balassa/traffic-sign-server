package hu.bme.aut.trafficsigns.endpoint

import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.api.response.model.LocatedDetection
import hu.bme.aut.trafficsigns.model.DetectedSign
import hu.bme.aut.trafficsigns.repository.DetectionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyDouble
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class StoredDetectionsIT {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var repository: DetectionRepository

    @Test
    fun getAllSigns() {
        `when`(repository.findAll()).thenReturn(listOf(
                DetectedSign().apply {
                    lat = 19.044; lon = 47.198
                    signClass = 30
                    confidence = 0.6
                }
        ))

        val resultList = webTestClient.get().uri("/signs")
                .exchange()
                .expectStatus().isOk
                .expectBodyList(LocatedDetection::class.java)
                .returnResult()
                .responseBody

        assertThat(resultList)
                .hasSize(1)
                .allSatisfy {
                    assertThat(it.confidence).isEqualTo(0.6)
                    assertThat(it.classification?.serial).isEqualTo(30)
                    assertThat(it.lat).isEqualTo(19.044)
                    assertThat(it.lon).isEqualTo(47.198)
                }
    }

    @Test
    fun getSignsByPosition() {
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(listOf(
                DetectedSign().apply {
                    lat = 1.0; lon = 2.0
                    signClass = 30
                    confidence = 0.6
                }
        ))

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
                .hasSize(1)
                .allSatisfy {
                    assertThat(it.confidence).isEqualTo(0.6)
                    assertThat(it.classification?.serial).isEqualTo(30)
                    assertThat(it.lat).isEqualTo(1.0)
                    assertThat(it.lon).isEqualTo(2.0)
                }
    }

    @Test
    fun getSignsBadRequest() {
        `when`(repository.findByLatBetweenAndLonBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(listOf(
                DetectedSign().apply {
                    lat = 1.0; lon = 2.0
                    signClass = 30
                    confidence = 0.6
                }
        ))

        val resultList = webTestClient.get().uri {
            it.path("/signs")
                    .queryParam("lat", 1.01)
                    .queryParam("lon", 2.01)
                    .build()
        }.exchange()
                .expectStatus().isBadRequest
                .expectBody().isEmpty
    }
}