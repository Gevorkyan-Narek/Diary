package com.cyclone.diary

import com.cyclone.diary.Presenter.Utilities
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class testsUtilities {
    @Test
    fun timeTest() {
        val time = LocalTime.of(14, 45)
        val leftBorder = Date.from(
            LocalDateTime.of(LocalDate.now(), LocalTime.parse("13:30"))
                .atZone(ZoneId.systemDefault()).toInstant()
        )
        val rightBorder = Date.from(
            LocalDateTime.of(LocalDate.now(), LocalTime.parse("16:30"))
                .atZone(ZoneId.systemDefault()).toInstant()
        )
        assertTrue(Utilities.checkTime(time, leftBorder, rightBorder))
    }

    @Test
    fun compareDateTest() {
        assertFalse(
            Utilities.comparisonDates(
                Date.from(Instant.now()),
                org.threeten.bp.LocalDate.of(2020, 5, 5)
            )
        )
    }
}