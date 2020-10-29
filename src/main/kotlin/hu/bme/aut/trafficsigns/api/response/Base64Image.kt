package hu.bme.aut.trafficsigns.api.response

class Base64Image (
        base64: String
) {
    val base64: String = "data:image/png;base64,${base64}"
}