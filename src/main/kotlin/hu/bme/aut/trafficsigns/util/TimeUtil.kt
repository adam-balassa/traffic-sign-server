package hu.bme.aut.trafficsigns.util

import java.util.*

fun <T> withTimeMeasure(run: () -> T): TimeMeasurement<T> {
    val start = Date().time
    val result = run()
    val end = Date().time
    return TimeMeasurement(end - start, result)
}

data class TimeMeasurement<T> (
        val time: Long,
        val result: T
)