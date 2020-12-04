package hu.bme.aut.trafficsigns.unit

import hu.bme.aut.trafficsigns.util.withTimeMeasure
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test

class TimeMeasureTest {
    @Test
    fun timeMeasure() {
        val result = withTimeMeasure { Thread.sleep(400) }
        assertThat(result.time).isCloseTo(400L, Offset.offset(2L))
    }
}