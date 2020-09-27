package hu.bme.aut.trafficsigns.api.response.model

import hu.bme.aut.trafficsigns.util.labels

data class Classification (
     val serial: Int
) {
    val label: String
    get () = labels[serial]

    init {
        require(serial in labels.indices)
    }
}