package hu.bme.aut.trafficsigns.dto.detector

import java.util.*

class DetectionResponse {
    lateinit var objects: List<List<Double>>
    lateinit var classifications: List<Int>
}