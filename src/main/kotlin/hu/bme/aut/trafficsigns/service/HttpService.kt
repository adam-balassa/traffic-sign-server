package hu.bme.aut.trafficsigns.service

import hu.bme.aut.trafficsigns.dto.detector.DetectionRequest
import hu.bme.aut.trafficsigns.dto.detector.DetectionResponse
import hu.bme.aut.trafficsigns.exception.InternalException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

private const val DETECT_ENDPOINT = "/detect"

@Service
class HttpService {
    @Value(value="\${detector.host}")
    private lateinit var host: String

    fun detect(path: String): DetectionResponse {
        val restTemplate = RestTemplate()
        val request = HttpEntity(DetectionRequest(path))
        val response = restTemplate.postForEntity(host + DETECT_ENDPOINT , request, DetectionResponse::class.java)
        return response.body ?: throw InternalException(2001, "Unable to connect to python server")
    }
}