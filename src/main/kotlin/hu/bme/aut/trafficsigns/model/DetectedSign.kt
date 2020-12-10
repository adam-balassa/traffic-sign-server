package hu.bme.aut.trafficsigns.model

import javax.persistence.*

@Entity
@Table(name="detections")
class DetectedSign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    @Column(precision = 8, scale = 6)
    var lat: Double = 0.0

    @Column(precision = 8, scale = 6)
    var lon: Double = 0.0

    var signClass: Int = -1

    var confidence: Double = 0.0
}