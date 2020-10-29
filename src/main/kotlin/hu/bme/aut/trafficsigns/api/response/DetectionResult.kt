package hu.bme.aut.trafficsigns.api.response

import hu.bme.aut.trafficsigns.api.response.model.Detection

data class DetectionResult (
        val objects: List<Detection>,
        val image: Base64Image,
        val executionTime: Double
){

}