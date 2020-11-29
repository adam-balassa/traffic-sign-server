package hu.bme.aut.trafficsigns.dto.detector

class DetectionResponse {
    lateinit var objects: List<List<Double>>
    lateinit var classifications: List<Int>
}