package hu.bme.aut.trafficsigns.mapping

import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.api.response.model.LocatedDetection
import hu.bme.aut.trafficsigns.model.DetectedSign
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface DtoToModelMapper {
    @Mapping(source = "sign.signClass", target = "classification.serial")
    fun modelToDetection(sign: DetectedSign): Detection
    fun modelToDetection(sign: List<DetectedSign>): List<Detection>

    @Mapping(source = "sign.signClass", target = "classification.serial")
    fun modelToLocatedDetection(sign: DetectedSign): LocatedDetection
    fun modelToLocatedDetection(sign: List<DetectedSign>): List<LocatedDetection>

    @Mapping(source = "detection.classification.serial", target = "signClass")
    fun detectionToModel(detection: Detection, lat: Double, lon: Double): DetectedSign

    companion object {
        val INSTANCE = Mappers.getMapper(DtoToModelMapper::class.java)

        fun detectionToModel(result: DetectionResult, lat: Double, lon: Double): List<DetectedSign> {
            return result.objects.map { detection: Detection -> INSTANCE.detectionToModel(detection, lat, lon) }
        }
    }
}