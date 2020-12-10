package hu.bme.aut.trafficsigns.api.request

data class DetectionQuery (
        val lat: Double?,
        val lon: Double?,
        val radius: Double?) {

    init {
        listOf(lat, lon, radius).run {
            require(all { it == null } || all { it != null })
        }
    }

    val isEmpty: Boolean
    get() = lat == null
}