package hu.bme.aut.trafficsigns.mapping

import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.model.DetectedSign
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface DtoToModelMapper {
    @Mapping(source = "sign.signClass", target = "classification.serial")
    fun modelToDto(sign: DetectedSign): Detection
    fun modelToDto(sign: List<DetectedSign>): List<Detection>

    @Mapping(source = "detection.classification.serial", target = "signClass")
    fun dtoToModel(detection: Detection, lat: Double, lon: Double): DetectedSign


    companion object {
        val INSTANCE = Mappers.getMapper(DtoToModelMapper::class.java)

        fun dtoToModel(result: DetectionResult, lat: Double, lon: Double): List<DetectedSign> {
            return result.objects.map { detection: Detection -> INSTANCE.dtoToModel(detection, lat, lon) }
        }
    }
}