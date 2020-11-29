package hu.bme.aut.trafficsigns.service

import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.Base64Image
import hu.bme.aut.trafficsigns.api.response.model.BoundingBox
import hu.bme.aut.trafficsigns.api.response.model.Classification
import hu.bme.aut.trafficsigns.api.response.model.Coordinates
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.dto.detector.DetectionResponse
import hu.bme.aut.trafficsigns.model.DetectedSign
import hu.bme.aut.trafficsigns.repository.DetectionRepository
import hu.bme.aut.trafficsigns.util.base64ToImage
import hu.bme.aut.trafficsigns.util.deleteImage
import hu.bme.aut.trafficsigns.util.imageToBase64
import hu.bme.aut.trafficsigns.util.withTimeMeasure
import mapping.DtoToModelMapper
import org.springframework.stereotype.Service
import java.util.*

@Service
class DetectionService (
        private val http: HttpService,
        private val repository: DetectionRepository
) {

    private final val random = Random()

    fun runDetection(base64Image: String): DetectionResult {
        val path = base64ToImage(base64Image)
        val response = withTimeMeasure {  http.detect(path) }
        deleteImage(path)
        return DetectionResult(
                response.result.toDetections(),
                Base64Image("data:image/png;base64,${base64Image}"),
                response.time.toDouble()
        )
    }

    fun runAndSaveDetection(image: String, lat: Double, lon: Double): DetectionResult {
        val result = runDetection(image)
        val previousDetections = findCloseDetections(lat, lon)

        refreshSavedDetections(result, previousDetections, lat, lon)

        val legitDetections = previousDetections.filter { it.confidence > 0.4 }
        val detectionsToAdd = legitDetections.filter { sign ->
            result.objects.all { sign.signClass != it.classification?.serial }
        }

        return DetectionResult(
                mutableListOf<Detection>().apply {
                    addAll(result.objects)
                    addAll(DtoToModelMapper.INSTANCE.modelToDto(detectionsToAdd))
                },
                result.image,
                result.executionTime
        )
    }

    private fun refreshSavedDetections(result: DetectionResult, previousDetections: List<DetectedSign>, lat: Double, lon: Double) {
        val detectionsToSave = mutableListOf<DetectedSign>()
        val detectionsToDelete = mutableListOf<DetectedSign>()

        for (detection in result.objects) {
            val previous = previousDetections.find { it.signClass == detection.classification?.serial }
            if (previous != null) {
                val common = (previous.confidence + detection.confidence) / 2
                previous.confidence = common + (1 - common) / 2
                previous.lat = lat; previous.lon = lon

                detection.confidence = previous.confidence
                detectionsToSave.add(previous)
            } else {
                detectionsToSave.add(DtoToModelMapper.INSTANCE.dtoToModel(detection, lat, lon))
            }
        }

        for (previous in previousDetections)
            if (result.objects.all { it.classification?.serial != previous.signClass }) {
                previous.confidence -= 0.1
                if (previous.confidence < 0.4)
                    detectionsToDelete.add(previous)
                else
                    detectionsToSave.add(previous)
            }

        repository.saveAll(detectionsToSave)
        repository.deleteAll(detectionsToDelete)
    }

    fun runRandomImageDetection(): DetectionResult {
        val images = listOf("testimage.png", "IMG_3763.jpg", "IMG_3869.jpg")
        val i = random.nextInt(images.size)
        val imageName = images[i]
        val image = imageToBase64(imageName)
        return runDetection(image)
    }

    private fun findCloseDetections(lat: Double, lon: Double): List<DetectedSign> {
        val d = 0.00007
        return repository.findByLatBetweenAndLonBetween(lat - d, lat + d, lon - d, lon + d)
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