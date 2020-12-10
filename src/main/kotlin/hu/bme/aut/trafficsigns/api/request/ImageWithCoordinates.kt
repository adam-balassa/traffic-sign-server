package hu.bme.aut.trafficsigns.api.request

class ImageWithCoordinates (
        image: String,
        val lat: Double,
        val lon: Double): Image(image)