package hu.bme.aut.trafficsigns.service

import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.Base64Image
import hu.bme.aut.trafficsigns.api.response.model.BoundingBox
import hu.bme.aut.trafficsigns.api.response.model.Classification
import hu.bme.aut.trafficsigns.api.response.model.Coordinates
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.dto.detector.DetectionResponse
import hu.bme.aut.trafficsigns.util.base64ToImage
import hu.bme.aut.trafficsigns.util.deleteImage
import hu.bme.aut.trafficsigns.util.imageToBase64
import hu.bme.aut.trafficsigns.util.withTimeMeasure
import net.coobird.thumbnailator.Thumbnailator
import org.springframework.stereotype.Service
import java.util.*

@Service
class DetectionService (
        private val http: HttpService
) {

    private final val random = Random()

    fun runDetection(base64Image: String): DetectionResult {
        val path = base64ToImage(base64Image)
        val response = withTimeMeasure {  http.detect(path) }
        deleteImage(path)
        return DetectionResult(
                response.result.toDetections(),
                Base64Image(base64Image),
                response.time.toDouble()
        )
    }

    fun runRandomImageDetection(): DetectionResult {
        val images = listOf("testimage.png", "IMG_3763.jpg", "IMG_3869.jpg")
        val i = random.nextInt(images.size)
        val imageName = images[i]
        val image = imageToBase64(imageName)
        return runDetection(image)
    }

    private fun DetectionResponse.toDetections(): List<Detection> {
        val detectedObjects = mutableListOf<Detection>()
        for (i in objects.indices) {
            val x = objects[i][1]
            val y = objects[i][2]
            val w = objects[i][3]
            val h = objects[i][4]
            val x1 = x - w / 2
            val x2 = x + w / 2
            val y1 = y - h / 2
            val y2 = y + h / 2
            detectedObjects.add(Detection(
                    boundingBox = BoundingBox(Coordinates(x1, y1), Coordinates(x2, y2)),
                    classification = Classification(classifications[i]),
                    confidence = objects[i][0]
            ))
        }
        return detectedObjects
    }
}