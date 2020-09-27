package hu.bme.aut.trafficsigns.controller

import hu.bme.aut.trafficsigns.api.request.Image
import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.model.BoundingBox
import hu.bme.aut.trafficsigns.api.response.model.Classification
import hu.bme.aut.trafficsigns.api.response.model.Coordinates
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.util.imageToBase64
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/image")
class ImageController {
    @PostMapping
    fun classifyImage(@RequestBody image: String): DetectionResult {
        return DetectionResult(
                listOf(Detection(
                        BoundingBox(Coordinates(10.0, 100.0), Coordinates(100.0, 200.0)),
                        Classification(0),
                        0.98
                )),
                DetectionResult.Base64Image(imageToBase64("testimage.png")),
                2345.0
        )
    }

    @PostMapping("/random")
    fun classifyRandomImage(): DetectionResult {
        return DetectionResult(
                listOf(Detection(
                        BoundingBox(Coordinates(10.0, 100.0), Coordinates(100.0, 200.0)),
                        Classification(0),
                        0.98
                )),
                DetectionResult.Base64Image(imageToBase64("testimage.png")),
                2345.0
        )
    }
}