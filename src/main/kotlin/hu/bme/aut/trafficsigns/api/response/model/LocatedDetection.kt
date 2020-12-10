package hu.bme.aut.trafficsigns.api.response.model

data class LocatedDetection (
        var classification: Classification?,
        var confidence: Double,
        var lat: Double,
        var lon: Double
) {
    constructor(): this(null, 0.0, 0.0, 0.0)
}