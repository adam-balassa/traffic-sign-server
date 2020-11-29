package hu.bme.aut.trafficsigns.repository

import hu.bme.aut.trafficsigns.model.DetectedSign
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DetectionRepository: JpaRepository<DetectedSign, Long> {
    fun findByLatBetweenAndLonBetween(lat1: Double, lat2: Double, lon1: Double, lon2: Double): List<DetectedSign>
}