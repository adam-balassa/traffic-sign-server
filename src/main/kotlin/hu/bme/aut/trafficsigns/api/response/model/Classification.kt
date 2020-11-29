package hu.bme.aut.trafficsigns.api.response.model

import hu.bme.aut.trafficsigns.util.labels

data class Classification (var serial: Int = -1) {

    val label: String
    get () = labels[serial]
}