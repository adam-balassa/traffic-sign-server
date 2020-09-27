package hu.bme.aut.trafficsigns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrafficSignsApplication

fun main(args: Array<String>) {
    runApplication<TrafficSignsApplication>(*args)
}
