package hu.bme.aut.trafficsigns.api.response.model

data class Detection (
    var boundingBox: BoundingBox?,
    var classification: Classification?,
    var confidence: Double
) {
    constructor(): this(null, null, 0.0)
}