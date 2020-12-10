package hu.bme.aut.trafficsigns.service

import hu.bme.aut.trafficsigns.dto.detector.DetectionRequest
import hu.bme.aut.trafficsigns.dto.detector.DetectionResponse
import hu.bme.aut.trafficsigns.exception.InvalidDetectorResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

const val DETECT_ENDPOINT = "/detect"

@Service
class HttpService {
    @Value(value="\${detector.host}")
    private lateinit var host: String

    fun detect(path: String): DetectionResponse {
        val restTemplate = RestTemplate()
        val request = HttpEntity(DetectionRequest(path))
        return try {
            val response = restTemplate.postForEntity(host + DETECT_ENDPOINT , request, DetectionResponse::class.java)
            response.body!!
        } catch (e: Throwable) {
            throw InvalidDetectorResponse(2001, "Invalid result from detector server")
        }
    }
}