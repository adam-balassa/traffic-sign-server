package hu.bme.aut.trafficsigns.api.response.model

data class Detection (
    val boundingBox: BoundingBox,
    val classification: Classification,
    val confidence: Double
)