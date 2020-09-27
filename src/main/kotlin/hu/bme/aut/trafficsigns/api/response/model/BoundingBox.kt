package hu.bme.aut.trafficsigns.api.response.model

data class BoundingBox (
        val topLeft: Coordinates,
        val bottomRight: Coordinates
)