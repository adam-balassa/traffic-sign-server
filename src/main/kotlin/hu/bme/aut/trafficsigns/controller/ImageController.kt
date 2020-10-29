package hu.bme.aut.trafficsigns.controller

import hu.bme.aut.trafficsigns.api.request.Image
import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.model.BoundingBox
import hu.bme.aut.trafficsigns.api.response.model.Classification
import hu.bme.aut.trafficsigns.api.response.model.Coordinates
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.service.DetectionService
import hu.bme.aut.trafficsigns.util.imageToBase64
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/image")
class ImageController (
        private val service: DetectionService
){
    @PostMapping
    fun classifyImage(@RequestBody image: Image): DetectionResult {
        return service.runDetection(image.image.removePrefix("data:image/png;base64,"))
    }

    @PostMapping("/random")
    fun classifyRandomImage(): DetectionResult {
        return service.runRandomImageDetection()
    }
}