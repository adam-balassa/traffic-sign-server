package hu.bme.aut.trafficsigns.endpoint

import com.github.tomakehurst.wiremock.client.WireMock.*
import hu.bme.aut.trafficsigns.service.DETECT_ENDPOINT

class DetectionServerMock {
    companion object {
        fun detectSuccess() {
            stubFor(post(urlEqualTo(DETECT_ENDPOINT))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withStatus(200)
                            .withBody("""{
                                | "objects": [[0.8, 3, 4, 2, 4], [0.9, 0.5, 0.5, 1, 1]],
                                | "classifications": [0, 42]
                            | }""".trimMargin())
                    )
            )
        }

        fun detectEmpty() {
            stubFor(post(urlEqualTo(DETECT_ENDPOINT))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withStatus(200)
                            .withBody("""{
                                | "objects": [],
                                | "classifications": []
                            | }""".trimMargin())
                    )
            )
        }

        fun detectFail() = stubFor(post(urlEqualTo(DETECT_ENDPOINT))
                .willReturn(aResponse().withStatus(500)))

    }
}