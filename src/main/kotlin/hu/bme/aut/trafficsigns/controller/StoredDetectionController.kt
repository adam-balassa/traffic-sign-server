package hu.bme.aut.trafficsigns.controller

import hu.bme.aut.trafficsigns.api.request.DetectionQuery
import hu.bme.aut.trafficsigns.api.response.model.Detection
import hu.bme.aut.trafficsigns.api.response.model.LocatedDetection
import hu.bme.aut.trafficsigns.mapping.DtoToModelMapper
import hu.bme.aut.trafficsigns.service.DetectionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/signs")
class StoredDetectionController (private val service: DetectionService) {

    @GetMapping
    fun getSignsByPosition(
            @RequestParam lat: Double?,
            @RequestParam lon: Double?,
            @RequestParam radius: Double?): List<LocatedDetection> {
        val query = DetectionQuery(lat, lon, radius)

        return DtoToModelMapper.INSTANCE.modelToLocatedDetection(
                if (query.isEmpty) service.getAllSigns()
                else service.getSignsByPosition(query)
        )
    }
}