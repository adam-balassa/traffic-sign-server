package hu.bme.aut.trafficsigns.unit

import hu.bme.aut.trafficsigns.api.response.Base64Image
import hu.bme.aut.trafficsigns.api.response.DetectionResult
import hu.bme.aut.trafficsigns.api.response.model.BoundingBox
import hu.bme.aut.trafficsigns.api.response.model.Classification
import hu.bme.aut.trafficsigns.api.response.model.Coordinates
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.model.DetectedSign
import hu.bme.aut.trafficsigns.mapping.DtoToModelMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DtoMappingTest {
    @Test
    fun dtoToModelTest() {
        val dto = Detection(
                boundingBox = BoundingBox(Coordinates(0.5, 1.0), Coordinates(2.0, 3.0)),
                classification = Classification(2),
                confidence = 0.5

        )

        val model = DtoToModelMapper.INSTANCE.detectionToModel(dto, 4.0, 5.0)

        assertThat(model.confidence).isEqualTo(0.5)
        assertThat(model.lat).isEqualTo(4.0)
        assertThat(model.lon).isEqualTo(5.0)
        assertThat(model.signClass).isEqualTo(2)
    }

    @Test
    fun dtoToModelsTest() {
        val dtos = listOf(
                Detection(
                        boundingBox = BoundingBox(Coordinates(0.5, 1.0), Coordinates(2.0, 3.0)),
                        classification = Classification(2),
                        confidence = 0.5),
                Detection(
                        boundingBox = BoundingBox(Coordinates(6.0, 7.0), Coordinates(8.0, 9.0)),
                        classification = Classification(40),
                        confidence = 0.9)
        )
        val detection = DetectionResult(dtos, Base64Image("image"), 0.66)

        val models = DtoToModelMapper.detectionToModel(detection, 4.0, 5.0)

        assertThat(models).hasSize(2)
        val model1 = models[0]
        val model2 = models[1]

        assertThat(model1.confidence).isEqualTo(0.5)
        assertThat(model1.lat).isEqualTo(4.0)
        assertThat(model1.lon).isEqualTo(5.0)
        assertThat(model1.signClass).isEqualTo(2)

        assertThat(model2.confidence).isEqualTo(0.9)
        assertThat(model2.lat).isEqualTo(4.0)
        assertThat(model2.lon).isEqualTo(5.0)
        assertThat(model2.signClass).isEqualTo(40)
    }

    @Test
    fun modelToDtoTest() {
        val model = DetectedSign().apply {
            lat = 1.0; lon = 2.0;
            confidence = 0.9
            signClass = 40
        }

        val dto = DtoToModelMapper.INSTANCE.modelToDetection(model)

        assertThat(dto.confidence).isEqualTo(0.9)
        assertThat(dto.classification?.serial).isEqualTo(40)
        assertThat(dto.boundingBox).isNull()
    }

    @Test
    fun modelToDtosTest() {
        val model = DetectedSign().apply {
            lat = 1.0; lon = 2.0;
            confidence = 0.9
            signClass = 40
        }

        val dto = DtoToModelMapper.INSTANCE.modelToDetection(listOf(model, model))

        assertThat(dto).allSatisfy {
            assertThat(it.confidence).isEqualTo(0.9)
            assertThat(it.classification?.serial).isEqualTo(40)
            assertThat(it.boundingBox).isNull()
        }.hasSize(2)
    }
}